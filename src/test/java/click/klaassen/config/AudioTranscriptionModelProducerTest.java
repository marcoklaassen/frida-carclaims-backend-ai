package click.klaassen.config;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.model.audio.AudioTranscriptionModel;
import org.junit.jupiter.api.Test;

class AudioTranscriptionModelProducerTest {

    private final AudioTranscriptionModelProducer producer = new AudioTranscriptionModelProducer();

    @Test
    void createsOpenAiModel() {
        producer.objectMapper = new ObjectMapper();
        AudioTranscriptionModel model = producer.audioTranscriptionModel(
                "https://api.openai.com/v1/",
                "test-api-key",
                "whisper-1");

        assertNotNull(model);
    }

    @Test
    void requiresBaseUrl() {
        producer.objectMapper = new ObjectMapper();
        assertThrows(IllegalStateException.class,
                () -> producer.audioTranscriptionModel("", "test-key", "whisper-1"));
        assertThrows(IllegalStateException.class,
                () -> producer.audioTranscriptionModel(null, "test-key", "whisper-1"));
    }

    @Test
    void requiresApiKey() {
        producer.objectMapper = new ObjectMapper();
        assertThrows(IllegalStateException.class,
                () -> producer.audioTranscriptionModel("https://api.openai.com/v1/", "", "whisper-1"));
        assertThrows(IllegalStateException.class,
                () -> producer.audioTranscriptionModel("https://api.openai.com/v1/", null, "whisper-1"));
    }

    @Test
    void allowsCustomModel() {
        producer.objectMapper = new ObjectMapper();
        assertDoesNotThrow(() -> producer.audioTranscriptionModel(
                "https://api.openai.com/v1/",
                "test-key",
                "custom-model"));
    }
}
