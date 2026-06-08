package click.klaassen.service;

import click.klaassen.api.VoiceExtractionResponse;
import click.klaassen.claims.model.Claimsdata;
import click.klaassen.exception.UpstreamAiException;
import com.fasterxml.jackson.core.JsonProcessingException;
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
            String whisperLanguage = language != null && !language.isBlank() ? language : DEFAULT_LANGUAGE;
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
            return objectMapper.readValue(currentStateJson, Claimsdata.class);
        } catch (JsonProcessingException e) {
            throw new jakarta.ws.rs.BadRequestException("Invalid currentState JSON");
        }
    }
}
