package click.klaassen.service;

import click.klaassen.claims.model.Claimsdata;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ClaimsJsonParser {

    private static final Pattern MARKDOWN_JSON = Pattern.compile("```(?:json)?\\s*([\\s\\S]*?)```", Pattern.CASE_INSENSITIVE);
    private static final String PLACEHOLDER = "not_specified";

    private static final Set<String> NOT_SPECIFIED_ALLOWED = Set.of(
            "hasVehicleDamage",
            "injuredPerson",
            "witnessExists",
            "comprehensiveInsurance",
            "inputTaxDeduction",
            "vehicleDrivingAbility",
            "formOfAddress");

    private ClaimsJsonParser() {
    }

    public static String extractJson(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Empty LLM response");
        }

        String trimmed = text.trim();

        Matcher markdownMatcher = MARKDOWN_JSON.matcher(trimmed);
        if (markdownMatcher.find()) {
            return markdownMatcher.group(1).trim();
        }

        int start = trimmed.indexOf('{');
        int end = trimmed.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return trimmed.substring(start, end + 1);
        }

        throw new IllegalArgumentException("No JSON object found in LLM response");
    }

    public static Claimsdata parse(ObjectMapper objectMapper, String text) throws JsonProcessingException {
        String json = extractJson(text);
        ObjectMapper lenient = objectMapper.copy()
                .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        String sanitized = sanitizeLlmPlaceholders(lenient, json);
        return lenient.readValue(sanitized, Claimsdata.class);
    }

    static String sanitizeLlmPlaceholders(ObjectMapper objectMapper, String json) throws JsonProcessingException {
        JsonNode root = objectMapper.readTree(json);
        if (!root.isObject()) {
            return json;
        }
        JsonNode sanitized = sanitizeNode(root, objectMapper);
        return objectMapper.writeValueAsString(sanitized);
    }

    private static JsonNode sanitizeNode(JsonNode node, ObjectMapper objectMapper) {
        if (node.isObject()) {
            return sanitizeObject((ObjectNode) node, objectMapper);
        }
        if (node.isArray()) {
            return sanitizeArray((ArrayNode) node, objectMapper);
        }
        return node;
    }

    private static ObjectNode sanitizeObject(ObjectNode node, ObjectMapper objectMapper) {
        ObjectNode sanitized = objectMapper.createObjectNode();
        Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            String fieldName = entry.getKey();
            JsonNode value = entry.getValue();

            if (value.isTextual() && PLACEHOLDER.equals(value.asText()) && !NOT_SPECIFIED_ALLOWED.contains(fieldName)) {
                continue;
            }

            JsonNode cleaned = sanitizeNode(value, objectMapper);
            if (cleaned.isObject() && cleaned.isEmpty()) {
                continue;
            }
            sanitized.set(fieldName, cleaned);
        }
        return sanitized;
    }

    private static ArrayNode sanitizeArray(ArrayNode node, ObjectMapper objectMapper) {
        ArrayNode sanitized = objectMapper.createArrayNode();
        for (JsonNode item : node) {
            sanitized.add(sanitizeNode(item, objectMapper));
        }
        return sanitized;
    }
}
