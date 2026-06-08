package click.klaassen.config;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class AudioTranscriptionModelProducerTest {

    private final AudioTranscriptionModelProducer producer = new AudioTranscriptionModelProducer();

    @Test
    void createsModelWithoutApiKey() {
        producer.objectMapper = new ObjectMapper();
        assertDoesNotThrow(() -> producer.audioTranscriptionModel(
                "http://localhost:51060/inference", Optional.empty()));
        assertDoesNotThrow(() -> producer.audioTranscriptionModel(
                "http://localhost:51060/inference", Optional.of("")));
    }

    @Test
    void createsModelWithApiKey() {
        producer.objectMapper = new ObjectMapper();
        assertDoesNotThrow(() -> producer.audioTranscriptionModel(
                "http://localhost:51060/inference", Optional.of("secret-key")));
    }

    @Test
    void requiresBaseUrl() {
        producer.objectMapper = new ObjectMapper();
        assertThrows(IllegalStateException.class,
                () -> producer.audioTranscriptionModel("", Optional.empty()));
    }
}
