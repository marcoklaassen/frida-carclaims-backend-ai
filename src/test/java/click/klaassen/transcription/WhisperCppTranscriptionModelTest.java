package click.klaassen.transcription;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WhisperCppTranscriptionModelTest {

    private WhisperCppTranscriptionModel model;

    @BeforeEach
    void setUp() {
        model = new WhisperCppTranscriptionModel(
                "http://localhost:9998/inference",
                java.util.Optional.empty(),
                new ObjectMapper());
    }

    @Test
    void parseTranscriptFromJson() throws Exception {
        String json = "{\"text\": \" Der Unfall war in Bedburg.\\n\"}";
        assertEquals("Der Unfall war in Bedburg.", model.parseTranscript(json));
    }

    @Test
    void parseTranscriptFromPlainText() throws Exception {
        assertEquals("Hello world", model.parseTranscript("Hello world"));
    }
}
