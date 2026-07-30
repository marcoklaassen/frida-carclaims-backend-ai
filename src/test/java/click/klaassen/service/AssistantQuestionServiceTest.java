package click.klaassen.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AssistantQuestionServiceTest {

    @Test
    void promptResourceLoadable() {
        var stream = getClass().getResourceAsStream("/frida/assistant-question-prompt.md");
        assertNotNull(stream, "Prompt resource must be on classpath");
    }

    @Test
    void promptContainsStepOrder() throws Exception {
        var stream = getClass().getResourceAsStream("/frida/assistant-question-prompt.md");
        String content = new String(stream.readAllBytes());
        assertTrue(content.contains("accident-info"));
        assertTrue(content.contains("accident-location"));
        assertTrue(content.contains("personal-info-a"));
        assertTrue(content.contains("vehicle-info-a"));
        assertTrue(content.contains("driver-info-a"));
        assertTrue(content.contains("damage-location-a"));
        assertTrue(content.contains("damage-description-a"));
        assertTrue(content.contains("personal-info-b"));
        assertTrue(content.contains("vehicle-info-b"));
        assertTrue(content.contains("driver-info-b"));
        assertTrue(content.contains("damage-location-b"));
        assertTrue(content.contains("damage-description-b"));
        assertTrue(content.contains("injured-persons"));
        assertTrue(content.contains("miscellaneous-damages"));
        assertTrue(content.contains("witnesses"));
    }

    @Test
    void promptContainsOutputFormat() throws Exception {
        var stream = getClass().getResourceAsStream("/frida/assistant-question-prompt.md");
        String content = new String(stream.readAllBytes());
        assertTrue(content.contains("\"targetFields\""));
        assertTrue(content.contains("\"done\""));
        assertTrue(content.contains("\"navigateTo\""));
    }

    @Test
    void promptContainsPhotoRecommendation() throws Exception {
        var stream = getClass().getResourceAsStream("/frida/assistant-question-prompt.md");
        String content = new String(stream.readAllBytes());
        assertTrue(content.contains("recommendPhoto"));
    }

    @Test
    void escapeJsonHandlesSpecialChars() {
        String input = "Wie heißen Sie?\nBitte \"antworten\" Sie.";
        String escaped = AssistantQuestionService.escapeJson(input);
        assertFalse(escaped.contains("\n"));
        assertFalse(escaped.contains("\"antworten\""));
        assertTrue(escaped.contains("\\n"));
        assertTrue(escaped.contains("\\\"antworten\\\""));
    }
}
