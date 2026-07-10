package click.klaassen.transcription;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.audio.Audio;
import dev.langchain4j.model.audio.AudioTranscriptionRequest;
import dev.langchain4j.model.audio.AudioTranscriptionResponse;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;

class OpenAiAudioTranscriptionModelTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void parsesJsonResponse() throws IOException {
        OpenAiAudioTranscriptionModel model = new OpenAiAudioTranscriptionModel(
                "https://api.openai.com/v1/", "test-key", "whisper-1", objectMapper);

        String json = "{\"text\":\"Ich hatte einen Unfall in Berlin\"}";
        String transcript = model.parseTranscript(json);

        assertEquals("Ich hatte einen Unfall in Berlin", transcript);
    }

    @Test
    void parsesPlainTextResponse() throws IOException {
        OpenAiAudioTranscriptionModel model = new OpenAiAudioTranscriptionModel(
                "https://api.openai.com/v1/", "test-key", "whisper-1", objectMapper);

        String plainText = "Ich hatte einen Unfall in Berlin";
        String transcript = model.parseTranscript(plainText);

        assertEquals("Ich hatte einen Unfall in Berlin", transcript);
    }

    @Test
    void requiresBaseUrl() {
        assertThrows(IllegalArgumentException.class,
                () -> new OpenAiAudioTranscriptionModel(null, "key", "whisper-1", objectMapper));
        assertThrows(IllegalArgumentException.class,
                () -> new OpenAiAudioTranscriptionModel("", "key", "whisper-1", objectMapper));
    }

    @Test
    void requiresApiKey() {
        assertThrows(IllegalArgumentException.class,
                () -> new OpenAiAudioTranscriptionModel("https://api.openai.com/v1/", null, "whisper-1",
                        objectMapper));
        assertThrows(IllegalArgumentException.class,
                () -> new OpenAiAudioTranscriptionModel("https://api.openai.com/v1/", "", "whisper-1",
                        objectMapper));
    }

    @Test
    void defaultsToWhisper1Model() {
        OpenAiAudioTranscriptionModel model = new OpenAiAudioTranscriptionModel(
                "https://api.openai.com/v1/", "test-key", null, objectMapper);

        assertNotNull(model);
    }

    @Test
    void normalizesBaseUrl() {
        // Without trailing slash
        OpenAiAudioTranscriptionModel model1 = new OpenAiAudioTranscriptionModel(
                "https://api.openai.com/v1", "test-key", "whisper-1", objectMapper);
        assertNotNull(model1);

        // With trailing slash
        OpenAiAudioTranscriptionModel model2 = new OpenAiAudioTranscriptionModel(
                "https://api.openai.com/v1/", "test-key", "whisper-1", objectMapper);
        assertNotNull(model2);
    }
}
