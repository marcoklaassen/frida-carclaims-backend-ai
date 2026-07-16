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
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApplicationScoped
public class ClaimsFieldExtractor {

    private static final Logger LOG = Logger.getLogger(ClaimsFieldExtractor.class.getName());
    private static final double EXTRACTION_TEMPERATURE = 0.1;
    private static final Pattern EMAIL_DOMAIN_FIX = Pattern.compile(
            "\\.([-\\w]+\\.(?:com|de|net|org|io|eu|at|ch|info|biz))$");

    @Inject
    ClaimsSchemaKnowledge schemaKnowledge;

    @Inject
    ChatModel chatModel;

    @Inject
    ObjectMapper objectMapper;

    private final ConcurrentHashMap<String, String> systemPromptCache = new ConcurrentHashMap<>();

    public Claimsdata extractFields(String transcript, String currentState, String stepKey) {
        String state = currentState != null && !currentState.isBlank() ? currentState : "{}";
        String systemPrompt = systemPromptForStep(stepKey);
        String userPrompt = """
                Transcript: %s
                Current form state (JSON, context only — do not copy unmentioned fields): %s
                Return only a JSON object with exact FRIDA Claimsdata field names for fields mentioned in the transcript.
                """.formatted(transcript, state);

        try {
            ChatResponse response = chatModel.chat(ChatRequest.builder()
                    .messages(SystemMessage.from(systemPrompt), UserMessage.from(userPrompt))
                    .parameters(OpenAiChatRequestParameters.builder()
                            .temperature(EXTRACTION_TEMPERATURE)
                            .responseFormat(ResponseFormat.JSON)
                            .build())
                    .build());

            String text = response.aiMessage().text();
            LOG.info("LLM extraction response length=" + (text != null ? text.length() : 0)
                    + ", stepKey=" + (stepKey != null ? stepKey : "(none)"));
            String json = ClaimsJsonParser.extractJson(text);
            logUnknownTopLevelFields(json);
            Claimsdata result = ClaimsJsonParser.parse(objectMapper, json);
            fixEmailFields(result);
            return result;
        } catch (JsonProcessingException | IllegalArgumentException e) {
            LOG.warning("Failed to parse claims JSON from LLM response: " + e.getMessage());
            throw new UpstreamAiException("Claims extraction failed: invalid JSON from LLM", e);
        } catch (Exception e) {
            throw new UpstreamAiException("Claims extraction failed", e);
        }
    }

    String systemPromptForStep(String stepKey) {
        String cacheKey = stepKey != null && !stepKey.isBlank() ? stepKey.trim() : "";
        return systemPromptCache.computeIfAbsent(cacheKey, key -> ClaimsSchemaPrompt.EXTRACTION_RULES
                + schemaKnowledge.getSchemaPromptSection(key.isEmpty() ? null : key));
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

    private void fixEmailFields(Claimsdata data) {
        data.setInsuranceHolderEmail(fixEmail(data.getInsuranceHolderEmail()));
        data.setOtherInsuranceHolderEmail(fixEmail(data.getOtherInsuranceHolderEmail()));
        data.setDriverEmail(fixEmail(data.getDriverEmail()));
        data.setOtherDriverEmail(fixEmail(data.getOtherDriverEmail()));
        if (data.getWitnesses() != null) {
            for (var witness : data.getWitnesses()) {
                witness.setEmail(fixEmail(witness.getEmail()));
            }
        }
    }

    String fixEmail(String value) {
        if (value == null || value.contains("@")) {
            return value;
        }
        Matcher m = EMAIL_DOMAIN_FIX.matcher(value);
        if (m.find()) {
            String fixed = m.replaceFirst("@$1");
            LOG.info("Fixed transcribed email: " + value + " → " + fixed);
            return fixed;
        }
        return value;
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
