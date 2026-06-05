package click.klaassen.service;

import click.klaassen.claims.model.Claimsdata;
import click.klaassen.exception.UpstreamAiException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatRequestParameters;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

@ApplicationScoped
public class ClaimsFieldExtractor {

    private static final Logger LOG = Logger.getLogger(ClaimsFieldExtractor.class.getName());

    @Inject
    ClaimsSchemaKnowledge schemaKnowledge;

    @Inject
    ChatModel chatModel;

    @Inject
    ObjectMapper objectMapper;

    private String systemPrompt;

    @PostConstruct
    void init() {
        systemPrompt = ClaimsSchemaPrompt.EXTRACTION_RULES + schemaKnowledge.getSchemaPromptSection();
    }

    public Claimsdata extractFields(String transcript, String currentState) {
        String state = currentState != null && !currentState.isBlank() ? currentState : "{}";
        String userPrompt = """
                Transcript: %s
                Current form state (JSON, may be empty): %s
                Return only a JSON object with exact FRIDA Claimsdata field names for fields mentioned in the transcript.
                """.formatted(transcript, state);

        try {
            ChatResponse response = chatModel.chat(ChatRequest.builder()
                    .messages(SystemMessage.from(systemPrompt), UserMessage.from(userPrompt))
                    .parameters(OpenAiChatRequestParameters.builder()
                            .responseFormat(ResponseFormat.JSON)
                            .customParameters(Map.of(
                                    "chat_template_kwargs", Map.of("enable_thinking", false)))
                            .build())
                    .build());

            String text = response.aiMessage().text();
            LOG.info("LLM extraction response length=" + (text != null ? text.length() : 0));
            String json = ClaimsJsonParser.extractJson(text);
            logUnknownTopLevelFields(json);
            return ClaimsJsonParser.parse(objectMapper, json);
        } catch (JsonProcessingException | IllegalArgumentException e) {
            LOG.warning("Failed to parse claims JSON from LLM response: " + e.getMessage());
            throw new UpstreamAiException("Claims extraction failed: invalid JSON from LLM", e);
        } catch (Exception e) {
            throw new UpstreamAiException("Claims extraction failed", e);
        }
    }

    private void logUnknownTopLevelFields(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);
            if (!root.isObject()) {
                return;
            }
            Set<String> known = knownTopLevelFields();
            root.fieldNames().forEachRemaining(name -> {
                if (!known.contains(name)) {
                    LOG.warning("LLM returned unknown Claimsdata field (ignored by parser): " + name);
                }
            });
        } catch (JsonProcessingException e) {
            LOG.fine("Could not inspect LLM JSON for unknown fields: " + e.getMessage());
        }
    }

    private Set<String> knownTopLevelFields() {
        Set<String> fields = new HashSet<>();
        try {
            for (PropertyDescriptor descriptor : Introspector.getBeanInfo(Claimsdata.class).getPropertyDescriptors()) {
                if (descriptor.getReadMethod() != null && !"class".equals(descriptor.getName())) {
                    fields.add(descriptor.getName());
                }
            }
        } catch (Exception e) {
            LOG.fine("Could not introspect Claimsdata fields: " + e.getMessage());
        }
        return fields;
    }
}
