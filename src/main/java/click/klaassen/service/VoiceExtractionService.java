package click.klaassen.service;

import click.klaassen.api.VoiceExtractionResponse;
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
public class VoiceExtractionService {

    private static final Logger LOG = Logger.getLogger(VoiceExtractionService.class.getName());
    private static final String DEFAULT_LANGUAGE = "de";

    @Inject
    AudioTranscriptionModel transcriptionModel;

    @Inject
    ClaimsFieldExtractor claimsFieldExtractor;

    @Inject
    ClaimsDataMerger claimsDataMerger;

    @Inject
    ObjectMapper objectMapper;

    public VoiceExtractionResponse extract(
            byte[] audioBytes, String mimeType, String currentStateJson, String language, String stepKey) {
        String transcript = transcribe(audioBytes, mimeType, language);
        Claimsdata currentState = parseCurrentState(currentStateJson);
        Claimsdata extracted = extractClaims(transcript, currentStateJson, stepKey);
        Claimsdata merged = claimsDataMerger.merge(currentState, extracted);

        LOG.info("Voice extraction completed: transcriptLength=" + transcript.length()
                + ", transcript=" + abbreviate(transcript, 200));

        return new VoiceExtractionResponse(transcript, merged);
    }

    private String transcribe(byte[] audioBytes, String mimeType, String language) {
        try {
            Audio audio = Audio.builder()
                    .base64Data(Base64.getEncoder().encodeToString(audioBytes))
                    .mimeType(mimeType != null ? mimeType : "audio/webm")
                    .build();

            AudioTranscriptionRequest.Builder requestBuilder = AudioTranscriptionRequest.builder(audio);
            String whisperLanguage = resolveWhisperLanguage(language);
            requestBuilder.language(whisperLanguage);

            return transcriptionModel.transcribe(requestBuilder.build()).text();
        } catch (Exception e) {
            throw new UpstreamAiException("Audio transcription failed", e);
        }
    }

    private Claimsdata extractClaims(String transcript, String currentStateJson, String stepKey) {
        String state = currentStateJson != null && !currentStateJson.isBlank() ? currentStateJson : "{}";
        return claimsFieldExtractor.extractFields(transcript, state, stepKey);
    }

    private static String abbreviate(String text, int maxLength) {
        if (text == null) {
            return "";
        }
        String singleLine = text.replace('\n', ' ').trim();
        if (singleLine.length() <= maxLength) {
            return singleLine;
        }
        return singleLine.substring(0, maxLength) + "...";
    }

    private Claimsdata parseCurrentState(String currentStateJson) {
        if (currentStateJson == null || currentStateJson.isBlank()) {
            return null;
        }
        try {
            ObjectMapper lenient = objectMapper.copy()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
            return lenient.readValue(currentStateJson, Claimsdata.class);
        } catch (JsonProcessingException e) {
            LOG.warning("Could not parse currentState, ignoring: " + e.getMessage());
            return null;
        }
    }

    private String resolveWhisperLanguage(String language) {
        if (language == null || language.isBlank()) {
            return DEFAULT_LANGUAGE;
        }
        try {
            return Language.fromValue(language).getIsoCode();
        } catch (IllegalArgumentException e) {
            return DEFAULT_LANGUAGE;
        }
    }
}
