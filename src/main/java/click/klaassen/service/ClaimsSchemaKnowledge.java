package click.klaassen.service;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.yaml.snakeyaml.Yaml;

@ApplicationScoped
public class ClaimsSchemaKnowledge {

    private static final String OAS_RESOURCE = "/frida/claimsOas.yaml";
    private static final String DESCRIPTION_RESOURCE = "/frida/descriptionClaim.md";
    private static final String HINTS_RESOURCE = "/frida/voice-mapping-hints.md";
    private static final int MAX_PROMPT_CHARS = 16_000;

    private static final Set<String> EXCLUDED_FIELDS = Set.of(
            "damagedCarImages",
            "damagedWindowImages",
            "certificateForWildlife");

    private static final String ROOT_SCHEMA = "Claimsdata";
    private static final String PERSON_SCHEMA = "Person";

    private String schemaPromptSection;

    @PostConstruct
    void init() {
        schemaPromptSection = buildSchemaPromptSection();
    }

    String getSchemaPromptSection() {
        return schemaPromptSection;
    }

    private String buildSchemaPromptSection() {
        Map<String, Object> schemas = loadSchemas();
        List<String> catalogLines = new ArrayList<>();
        walkSchema(schemas, resolveSchema(schemas, asMap(schemas.get(ROOT_SCHEMA))), "", catalogLines);

        StringBuilder prompt = new StringBuilder();
        prompt.append("Domain context:\n");
        prompt.append(readResource(DESCRIPTION_RESOURCE).trim());
        prompt.append("\n\nFRIDA Claimsdata field catalog (use ONLY these exact JSON paths and field names):\n");
        catalogLines.forEach(line -> prompt.append("- ").append(line).append('\n'));
        prompt.append("\nVoice mapping hints:\n");
        prompt.append(readResource(HINTS_RESOURCE).trim());
        prompt.append('\n');

        String result = prompt.toString();
        if (result.length() > MAX_PROMPT_CHARS) {
            throw new IllegalStateException(
                    "Schema prompt section exceeds " + MAX_PROMPT_CHARS + " chars (" + result.length() + ")");
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> loadSchemas() {
        Yaml yaml = new Yaml();
        try (InputStream in = resourceStream(OAS_RESOURCE)) {
            Map<String, Object> root = yaml.load(in);
            Map<String, Object> components = (Map<String, Object>) root.get("components");
            return (Map<String, Object>) components.get("schemas");
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load " + OAS_RESOURCE, e);
        }
    }

    @SuppressWarnings("unchecked")
    private void walkSchema(
            Map<String, Object> schemas,
            Map<String, Object> schema,
            String prefix,
            List<String> catalogLines) {
        Map<String, Object> properties = (Map<String, Object>) schema.get("properties");
        if (properties == null) {
            return;
        }

        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            String fieldName = entry.getKey();
            if (EXCLUDED_FIELDS.contains(fieldName)) {
                continue;
            }

            String path = prefix.isEmpty() ? fieldName : prefix + fieldName;
            Map<String, Object> property = asMap(entry.getValue());

            if (property.containsKey("$ref")) {
                Map<String, Object> resolved = resolveSchema(schemas, property);
                String schemaName = refSchemaName(property);
                if (PERSON_SCHEMA.equals(schemaName)) {
                    appendPersonFields(resolved, prefix, catalogLines);
                } else {
                    walkSchema(schemas, resolved, path + ".", catalogLines);
                }
                continue;
            }

            String type = stringValue(property.get("type"));
            if ("array".equals(type)) {
                Map<String, Object> items = asMap(property.get("items"));
                if (items.containsKey("$ref")) {
                    Map<String, Object> resolved = resolveSchema(schemas, items);
                    walkSchema(schemas, resolved, path + "[].", catalogLines);
                } else {
                    catalogLines.add(formatFieldLine(path, property, items));
                }
                continue;
            }

            catalogLines.add(formatFieldLine(path, property, property));
        }
    }

    private void appendPersonFields(Map<String, Object> personSchema, String parentPrefix, List<String> catalogLines) {
        Map<String, Object> properties = asMap(personSchema.get("properties"));
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            String path = parentPrefix + "personalInformation." + entry.getKey();
            Map<String, Object> property = asMap(entry.getValue());
            catalogLines.add(formatFieldLine(path, property, property));
        }
    }

    private String refSchemaName(Map<String, Object> node) {
        String ref = stringValue(node.get("$ref"));
        if (ref == null) {
            return null;
        }
        return ref.substring(ref.lastIndexOf('/') + 1);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> resolveSchema(Map<String, Object> schemas, Map<String, Object> node) {
        String ref = stringValue(node.get("$ref"));
        if (ref == null) {
            return node;
        }
        String schemaName = ref.substring(ref.lastIndexOf('/') + 1);
        Map<String, Object> resolved = asMap(schemas.get(schemaName));
        if (resolved.isEmpty()) {
            throw new IllegalStateException("Unresolved schema reference: " + ref);
        }
        return resolved;
    }

    private String formatFieldLine(String path, Map<String, Object> property, Map<String, Object> valueSource) {
        String description = cleanDescription(stringValue(property.get("description")));
        if (description == null) {
            description = cleanDescription(stringValue(valueSource.get("description")));
        }

        StringBuilder line = new StringBuilder(path);
        line.append(" | ");
        line.append(description != null ? description : "(no description)");

        String type = stringValue(valueSource.get("type"));
        if (type != null) {
            line.append(" | ").append(type);
            String format = stringValue(valueSource.get("format"));
            if (format != null) {
                line.append('/').append(format);
            }
        }

        List<String> enumValues = enumValues(valueSource.get("enum"));
        if (enumValues.isEmpty()) {
            enumValues = enumValues(property.get("enum"));
        }
        if (!enumValues.isEmpty()) {
            line.append(" | enum: ").append(String.join(", ", enumValues));
        }

        Object example = valueSource.get("example");
        if (example == null) {
            example = property.get("example");
        }
        if (example != null) {
            line.append(" | example: ").append(example);
        }

        return line.toString();
    }

    @SuppressWarnings("unchecked")
    private List<String> enumValues(Object enumNode) {
        if (!(enumNode instanceof List<?> values)) {
            return List.of();
        }
        List<String> result = new ArrayList<>();
        for (Object value : values) {
            if (value != null) {
                result.add(String.valueOf(value));
            }
        }
        return result;
    }

    private String cleanDescription(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }
        int marker = description.indexOf("||");
        if (marker >= 0) {
            description = description.substring(0, marker);
        }
        return description.trim();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> asMap(Object value) {
        if (value instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        return Map.of();
    }

    private String stringValue(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private String readResource(String path) {
        try (InputStream in = resourceStream(path)) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read resource " + path, e);
        }
    }

    private InputStream resourceStream(String path) throws IOException {
        InputStream in = ClaimsSchemaKnowledge.class.getResourceAsStream(path);
        if (in == null) {
            throw new IOException("Resource not found: " + path);
        }
        return in;
    }
}
