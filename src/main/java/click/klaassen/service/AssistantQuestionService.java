package click.klaassen.service;

import click.klaassen.exception.UpstreamAiException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatRequestParameters;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class AssistantQuestionService {

    private static final Logger LOG = Logger.getLogger(AssistantQuestionService.class.getName());
    private static final String PROMPT_RESOURCE = "/frida/assistant-question-prompt.md";

    @Inject
    ChatModel chatModel;

    @Inject
    ObjectMapper objectMapper;

    private volatile String systemPrompt;

    public AssistantQuestionResponse generateNextQuestion(
            String currentStateJson, String conversationHistoryJson) {
        String userPrompt = buildUserPrompt(currentStateJson, conversationHistoryJson);

        try {
            ChatResponse response = chatModel.chat(ChatRequest.builder()
                    .messages(
                            SystemMessage.from(getSystemPrompt()),
                            UserMessage.from(userPrompt))
                    .parameters(OpenAiChatRequestParameters.builder()
                            .temperature(0.3)
                            .responseFormat(ResponseFormat.JSON)
                            .build())
                    .build());

            String json = response.aiMessage().text();
            LOG.info("Question generation response length=" + (json != null ? json.length() : 0));
            return parseResponse(json);
        } catch (Exception e) {
            throw new UpstreamAiException("Question generation failed", e);
        }
    }

    private String buildUserPrompt(String currentStateJson, String conversationHistoryJson) {
        StringBuilder sb = new StringBuilder();
        sb.append("Current form state (fields already filled):\n");
        sb.append(currentStateJson != null ? currentStateJson : "{}");
        sb.append("\n\nConversation history so far:\n");
        sb.append(conversationHistoryJson != null ? conversationHistoryJson : "[]");
        sb.append("\n\nDetermine which fields are still missing. ");
        sb.append("Generate the next question following the step order. ");
        sb.append("Skip steps where all fields are already filled.");
        return sb.toString();
    }

    private AssistantQuestionResponse parseResponse(String json) {
        if (json == null || json.isBlank()) {
            return new AssistantQuestionResponse(
                    "Entschuldigung, ich konnte keine Frage generieren. Können wir es nochmal versuchen?",
                    null, null, List.of(), false, false, null, null, false, false);
        }
        try {
            String cleaned = ClaimsJsonParser.extractJson(json);
            ObjectMapper lenient = objectMapper.copy()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return lenient.readValue(cleaned, AssistantQuestionResponse.class);
        } catch (Exception e) {
            LOG.warning("Failed to parse question response: " + e.getMessage());
            return new AssistantQuestionResponse(
                    "Entschuldigung, ich konnte keine Frage generieren. Können wir es nochmal versuchen?",
                    null, null, List.of(), false, false, null, null, false, false);
        }
    }

    static String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private String getSystemPrompt() {
        if (systemPrompt == null) {
            synchronized (this) {
                if (systemPrompt == null) {
                    try (InputStream in = getClass().getResourceAsStream(PROMPT_RESOURCE)) {
                        if (in == null) {
                            throw new IOException("Resource not found: " + PROMPT_RESOURCE);
                        }
                        systemPrompt = new String(in.readAllBytes(), StandardCharsets.UTF_8).trim();
                    } catch (IOException e) {
                        throw new IllegalStateException("Failed to load " + PROMPT_RESOURCE, e);
                    }
                }
            }
        }
        return systemPrompt;
    }
}
