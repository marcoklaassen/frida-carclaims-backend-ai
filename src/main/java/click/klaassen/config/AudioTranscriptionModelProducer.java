package click.klaassen.config;

import click.klaassen.transcription.OpenAiAudioTranscriptionModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.model.audio.AudioTranscriptionModel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class AudioTranscriptionModelProducer {

    @Inject
    ObjectMapper objectMapper;

    @Produces
    @ApplicationScoped
    public AudioTranscriptionModel audioTranscriptionModel(
            @ConfigProperty(name = "voice.transcription.base-url") String baseUrl,
            @ConfigProperty(name = "voice.transcription.api-key") String apiKey,
            @ConfigProperty(name = "voice.transcription.model", defaultValue = "whisper-1") String model) {

        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalStateException(
                    "Audio transcription is not configured: set TRANSCRIPTION_BASE_URL (voice.transcription.base-url)");
        }

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "Audio transcription API key is not configured: set TRANSCRIPTION_API_KEY (voice.transcription.api-key)");
        }

        return new OpenAiAudioTranscriptionModel(baseUrl, apiKey, model, objectMapper);
    }
}
