package click.klaassen.config;

import click.klaassen.transcription.WhisperCppTranscriptionModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.model.audio.AudioTranscriptionModel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import java.util.Optional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class AudioTranscriptionModelProducer {

    @Inject
    ObjectMapper objectMapper;

    @Produces
    @ApplicationScoped
    public AudioTranscriptionModel audioTranscriptionModel(
            @ConfigProperty(name = "voice.transcription.base-url") String baseUrl,
            @ConfigProperty(name = "voice.transcription.api-key", defaultValue = "") Optional<String> apiKey) {

        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalStateException(
                    "Whisper transcription is not configured: set WHISPER_BASE_URL (voice.transcription.base-url)");
        }

        return new WhisperCppTranscriptionModel(baseUrl, apiKey, objectMapper);
    }
}
