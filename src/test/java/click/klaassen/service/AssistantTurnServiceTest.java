package click.klaassen.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class AssistantTurnServiceTest {

    @Test
    void enrichTranscriptWithContext() {
        String result = AssistantTurnService.enrichTranscriptWithContext(
                "Ja, das war am 15. März.",
                "Wann ist der Unfall passiert?");
        assertNotNull(result);
        assertEquals(
                "Der Assistent hat gefragt: \"Wann ist der Unfall passiert?\"\nDie Antwort war: \"Ja, das war am 15. März.\"",
                result);
    }

    @Test
    void enrichTranscriptWithNullQuestion() {
        String result = AssistantTurnService.enrichTranscriptWithContext(
                "Hallo, ich hatte einen Unfall.", null);
        assertEquals("Hallo, ich hatte einen Unfall.", result);
    }

    @Test
    void enrichTranscriptWithBlankQuestion() {
        String result = AssistantTurnService.enrichTranscriptWithContext(
                "Hallo", "  ");
        assertEquals("Hallo", result);
    }
}
