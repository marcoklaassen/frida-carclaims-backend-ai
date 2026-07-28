package click.klaassen.service;

import click.klaassen.api.AssistantTurnResponse;
import click.klaassen.claims.model.Claimsdata;
import click.klaassen.claims.model.enums.Language;
import click.klaassen.exception.UpstreamAiException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.audio.Audio;
import dev.langchain4j.model.audio.AudioTranscriptionModel;
import dev.langchain4j.model.audio.AudioTranscriptionRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Base64;
import java.util.logging.Logger;

@ApplicationScoped
public class AssistantTurnService {

    private static final Logger LOG = Logger.getLogger(AssistantTurnService.class.getName());

    private static final java.util.Map<String, String> STEP_KEY_TO_ROUTE = java.util.Map.of(
        "carclaimsDetails", "/accidentinfo",
        "insurance-holder-a", "/personalinfo/a",
        "driver-a", "/driverinfo/a",
        "insurance-holder-b", "/personalinfo/b",
        "driver-b", "/driverinfo/b",
        "injuredDetails", "/injuredpersons",
        "miscellaneousDamages", "/miscellaneousdamages",
        "witness", "/witnesses"
    );

    @Inject
    AudioTranscriptionModel transcriptionModel;

    @Inject
    ClaimsFieldExtractor claimsFieldExtractor;

    @Inject
    ClaimsDataMerger claimsDataMerger;

    @Inject
    AssistantQuestionService questionService;

    @Inject
    TextToSpeechService ttsService;

    @Inject
    TtsAudioCache ttsAudioCache;

    @Inject
    ObjectMapper objectMapper;

    public AssistantTurnResponse processTurn(
            byte[] audioBytes, String mimeType, String currentStateJson,
            String conversationHistoryJson, String previousStepKey, String previousQuestion) {

        Claimsdata currentState = parseCurrentState(currentStateJson);
        String transcript = null;

        if (audioBytes != null && audioBytes.length > 0) {
            transcript = transcribe(audioBytes, mimeType);
            String enriched = enrichTranscriptWithContext(transcript, previousQuestion);
            Claimsdata extracted = claimsFieldExtractor.extractFields(
                    enriched, currentStateJson, previousStepKey);
            currentState = claimsDataMerger.merge(currentState, extracted);
        }

        String updatedStateJson = serializeState(currentState);
        AssistantQuestionResponse questionResponse =
                questionService.generateNextQuestion(updatedStateJson, conversationHistoryJson);

        String audioUrl = null;
        if (questionResponse.question() != null && !questionResponse.question().isBlank()) {
            byte[] ttsAudio = ttsService.generateSpeech(questionResponse.question());
            String audioId = ttsAudioCache.store(ttsAudio);
            audioUrl = "/api/assistant/audio/" + audioId;
        }

        LOG.info("Assistant turn completed: transcript="
                + (transcript != null ? transcript.length() : 0)
                + " chars, nextStep=" + questionResponse.stepKey()
                + ", done=" + questionResponse.done());

        String navigateTo = STEP_KEY_TO_ROUTE.getOrDefault(
                questionResponse.stepKey(), questionResponse.navigateTo());

        return new AssistantTurnResponse(
                questionResponse.question(),
                questionResponse.stepKey(),
                navigateTo,
                audioUrl,
                currentState,
                transcript,
                questionResponse.done(),
                questionResponse.recommendPhoto()
                        ? questionResponse.photoReason() : null);
    }

    static String enrichTranscriptWithContext(String transcript, String previousQuestion) {
        if (previousQuestion == null || previousQuestion.isBlank()) {
            return transcript;
        }
        return "Der Assistent hat gefragt: \"" + previousQuestion
                + "\"\nDie Antwort war: \"" + transcript + "\"";
    }

    private String transcribe(byte[] audioBytes, String mimeType) {
        try {
            String base64 = Base64.getEncoder().encodeToString(audioBytes);
            String mime = mimeType != null ? mimeType : "audio/webm";
            Audio audio = Audio.builder()
                    .base64Data(base64)
                    .mimeType(mime)
                    .build();
            AudioTranscriptionRequest request = AudioTranscriptionRequest.builder(audio)
                    .language(Language.DE.getIsoCode())
                    .build();
            return transcriptionModel.transcribe(request).text();
        } catch (Exception e) {
            throw new UpstreamAiException("Audio transcription failed", e);
        }
    }

    private Claimsdata parseCurrentState(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            ObjectMapper lenient = objectMapper.copy()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
            return lenient.readValue(json, Claimsdata.class);
        } catch (JsonProcessingException e) {
            LOG.warning("Could not parse currentState: " + e.getMessage());
            return null;
        }
    }

    private String serializeState(Claimsdata state) {
        if (state == null) {
            return "{}";
        }
        try {
            return objectMapper.writeValueAsString(state);
        } catch (JsonProcessingException e) {
            LOG.warning("Could not serialize state: " + e.getMessage());
            return "{}";
        }
    }
}
