package click.klaassen.service;

import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.yaml.snakeyaml.Yaml;

@ApplicationScoped
@Startup
public class ClaimsSchemaKnowledge {

    private static final String OAS_RESOURCE = "/frida/claimsOas.yaml";
    private static final String DESCRIPTION_RESOURCE = "/frida/descriptionClaim.md";
    private static final String HINTS_RESOURCE = "/frida/voice-mapping-hints.md";
    private static final String STEP_CATALOG_RESOURCE = "/frida/step-field-catalog.yaml";
    private static final String SYNONYMS_RESOURCE = "/frida/german-field-synonyms.md";
    private static final String EXAMPLES_RESOURCE = "/frida/voice-extraction-examples.md";
    private static final int MAX_PROMPT_CHARS = 16_000;

    private static final Set<String> EXCLUDED_FIELDS = Set.of();

    private static final String ROOT_SCHEMA = "Claimsdata";

    private List<String> fullCatalogLines;
    private Map<String, StepConfig> stepConfigs;
    private String domainContext;
    private String voiceHints;
    private String germanSynonyms;
    private String extractionExamples;

    @PostConstruct
    void init() {
        domainContext = readResource(DESCRIPTION_RESOURCE).trim();
        voiceHints = readResource(HINTS_RESOURCE).trim();
        germanSynonyms = readResource(SYNONYMS_RESOURCE).trim();
        extractionExamples = readResource(EXAMPLES_RESOURCE).trim();
        stepConfigs = loadStepCatalog();
        fullCatalogLines = buildCatalogLines(loadSchemas());
        validatePromptSize(getSchemaPromptSection(null));
        validatePromptSize(getSchemaPromptSection("carclaimsDetails"));
        validatePromptSize(getSchemaPromptSection("driver-a"));
    }

    String getSchemaPromptSection() {
        return getSchemaPromptSection(null);
    }

    String getSchemaPromptSection(String stepKey) {
        StepConfig step = resolveStep(stepKey);
        if (step == null) {
            return buildFallbackPrompt(fullCatalogLines);
        }
        return buildStepPrompt(stepKey, step);
    }

    private String buildFallbackPrompt(List<String> catalogLines) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Domain context:\n");
        prompt.append(domainContext);
        prompt.append("\n\nFRIDA Claimsdata field catalog (use ONLY these exact JSON paths and field names):\n");
        List<String> fittedCatalog = fitCatalogLines(catalogLines, domainContext.length() + voiceHints.length() + 512);
        fittedCatalog.forEach(line -> prompt.append("- ").append(line).append('\n'));
        if (fittedCatalog.size() < catalogLines.size()) {
            prompt.append("- (catalog truncated: ")
                    .append(catalogLines.size() - fittedCatalog.size())
                    .append(" fields omitted)\n");
        }
        prompt.append("\nVoice mapping hints:\n");
        prompt.append(voiceHints);
        prompt.append('\n');
        return validateAndReturn(prompt.toString());
    }

    private String buildStepPrompt(String stepKey, StepConfig step) {
        List<String> catalogLines = filterCatalogLines(fullCatalogLines, step.allowedPathPrefixes());
        String examples = examplesForStep(stepKey);

        StringBuilder prompt = new StringBuilder();
        prompt.append("Domain context:\n");
        prompt.append(domainContext);
        prompt.append("\n\nFRIDA Claimsdata field catalog (use ONLY these exact JSON paths and field names):\n");

        int fixedPrefixLength = estimateStepFixedSectionsLength(step, stepKey, examples);
        List<String> fittedCatalog = fitCatalogLines(catalogLines, fixedPrefixLength);
        fittedCatalog.forEach(line -> prompt.append("- ").append(line).append('\n'));
        if (fittedCatalog.size() < catalogLines.size()) {
            prompt.append("- (catalog truncated: ")
                    .append(catalogLines.size() - fittedCatalog.size())
                    .append(" fields omitted)\n");
        }

        if (step.hints() != null && !step.hints().isBlank()) {
            prompt.append("\nStep-specific hints (").append(stepKey).append("):\n");
            prompt.append(step.hints().trim());
            prompt.append('\n');
        }

        prompt.append("\nVoice mapping hints:\n");
        prompt.append(voiceHints);
        prompt.append("\n\nGerman field synonyms (spoken phrases → schema paths and enums):\n");
        prompt.append(germanSynonyms);
        prompt.append("\n\nFew-shot extraction examples:\n");
        prompt.append(examples);
        prompt.append('\n');

        return validateAndReturn(prompt.toString());
    }

    private String validateAndReturn(String result) {
        if (result.length() > MAX_PROMPT_CHARS) {
            throw new IllegalStateException(
                    "Schema prompt section exceeds " + MAX_PROMPT_CHARS + " chars (" + result.length() + ")");
        }
        return result;
    }

    private int estimateStepFixedSectionsLength(StepConfig step, String stepKey, String examples) {
        int length = domainContext.length()
                + voiceHints.length()
                + germanSynonyms.length()
                + examples.length()
                + 512;
        if (step.hints() != null) {
            length += step.hints().length() + stepKey.length() + 64;
        }
        return length;
    }

    private List<String> fitCatalogLines(List<String> catalogLines, int fixedSectionsLength) {
        int budget = MAX_PROMPT_CHARS - fixedSectionsLength;
        if (budget <= 0) {
            return List.of();
        }

        List<String> fitted = new ArrayList<>();
        int used = 0;
        for (String line : catalogLines) {
            int lineCost = line.length() + 2;
            if (used + lineCost > budget) {
                break;
            }
            fitted.add(line);
            used += lineCost;
        }
        return fitted;
    }

    private String examplesForStep(String stepKey) {
        if (stepKey == null || stepKey.isBlank()) {
            return extractionExamples;
        }
        String key = stepKey.trim();
        StringBuilder filtered = new StringBuilder();
        String[] blocks = extractionExamples.split("(?=### Example:)");
        for (String block : blocks) {
            if (block.isBlank()) {
                continue;
            }
            if (block.contains("### Example:" + key) || block.contains("### Example: " + key)) {
                filtered.append(block.trim()).append("\n\n");
            } else if (block.contains("### Example: negative")) {
                filtered.append(block.trim()).append("\n\n");
            }
        }
        if (filtered.isEmpty()) {
            return extractionExamples;
        }
        return filtered.toString().trim();
    }

    private StepConfig resolveStep(String stepKey) {
        if (stepKey == null || stepKey.isBlank()) {
            return null;
        }
        return stepConfigs.get(stepKey.trim());
    }

    private List<String> filterCatalogLines(List<String> catalogLines, List<String> allowedPrefixes) {
        List<String> filtered = new ArrayList<>();
        for (String line : catalogLines) {
            String path = line.substring(0, line.indexOf(" | "));
            if (matchesAllowedPath(path, allowedPrefixes)) {
                filtered.add(line);
            }
        }
        return filtered;
    }

    private boolean matchesAllowedPath(String path, List<String> allowedPrefixes) {
        for (String prefix : allowedPrefixes) {
            if (prefix.endsWith(".")) {
                if (path.startsWith(prefix)) {
                    return true;
                }
            } else if (path.equals(prefix) || path.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private Map<String, StepConfig> loadStepCatalog() {
        Yaml yaml = new Yaml();
        try (InputStream in = resourceStream(STEP_CATALOG_RESOURCE)) {
            Map<String, Object> root = yaml.load(in);
            Map<String, Object> steps = (Map<String, Object>) root.get("steps");
            if (steps == null) {
                throw new IllegalStateException("step-field-catalog.yaml missing 'steps'");
            }

            Map<String, StepConfig> configs = new LinkedHashMap<>();
            for (Map.Entry<String, Object> entry : steps.entrySet()) {
                Map<String, Object> step = asMap(entry.getValue());
                List<String> prefixes = stringList(step.get("allowedPathPrefixes"));
                String hints = stringValue(step.get("hints"));
                configs.put(entry.getKey(), new StepConfig(prefixes, hints));
            }
            return Map.copyOf(configs);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load " + STEP_CATALOG_RESOURCE, e);
        }
    }

    private List<String> buildCatalogLines(Map<String, Object> schemas) {
        List<String> catalogLines = new ArrayList<>();
        walkSchema(schemas, resolveSchema(schemas, asMap(schemas.get(ROOT_SCHEMA))), "", catalogLines);
        return List.copyOf(catalogLines);
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
                walkSchema(schemas, resolved, path + ".", catalogLines);
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

    @SuppressWarnings("unchecked")
    private List<String> stringList(Object value) {
        if (!(value instanceof List<?> list)) {
            return List.of();
        }
        List<String> result = new ArrayList<>();
        for (Object item : list) {
            if (item != null) {
                result.add(String.valueOf(item));
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

    private void validatePromptSize(String prompt) {
        if (prompt.length() > MAX_PROMPT_CHARS) {
            throw new IllegalStateException(
                    "Schema prompt section exceeds " + MAX_PROMPT_CHARS + " chars (" + prompt.length() + ")");
        }
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

    record StepConfig(List<String> allowedPathPrefixes, String hints) {
    }

    List<String> filteredCatalogLinesForStep(String stepKey) {
        StepConfig step = resolveStep(stepKey);
        if (step == null) {
            return fullCatalogLines;
        }
        return filterCatalogLines(fullCatalogLines, step.allowedPathPrefixes());
    }
}
