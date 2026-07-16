package click.klaassen.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.List;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ClaimsSchemaKnowledgeTest {

    private static final int MAX_PROMPT_CHARS = 16_000;

    @Inject
    ClaimsSchemaKnowledge schemaKnowledge;

    @Test
    void catalogContainsCriticalFieldPaths() {
        String section = schemaKnowledge.getSchemaPromptSection();

        assertTrue(section.contains("accidentCity | Ort"));
        assertTrue(section.contains("licensePlate |"));
        assertTrue(section.contains("damageType |"));
        assertTrue(section.contains("insuranceHolderSurName |"));
    }

    @Test
    void catalogIncludesEnumValues() {
        String section = schemaKnowledge.getSchemaPromptSection();

        assertTrue(section.contains("miscellaneousDamages"));
        assertTrue(section.contains("enum: not_specified, false, true"));
        assertTrue(section.contains("enum: Auffahren, Rangieren/ Parken"));
    }

    @Test
    void promptSectionIncludesDomainContextAndVoiceHints() {
        String section = schemaKnowledge.getSchemaPromptSection();

        assertTrue(section.contains("Domain context:"));
        assertTrue(section.contains("FRIDA Schaden API"));
        assertTrue(section.contains("Voice mapping hints:"));
    }

    @Test
    void stepPromptIncludesSynonymsAndExamples() {
        String section = schemaKnowledge.getSchemaPromptSection("carclaimsDetails");

        assertTrue(section.contains("German field synonyms"));
        assertTrue(section.contains("Few-shot extraction examples"));
    }

    @Test
    void promptSectionStaysWithinSizeLimit() {
        assertTrue(schemaKnowledge.getSchemaPromptSection().length() < MAX_PROMPT_CHARS);
    }

    @Test
    void stepScopedCatalogFiltersToAccidentFieldsOnly() {
        List<String> catalogLines = schemaKnowledge.filteredCatalogLinesForStep("carclaimsDetails");
        String catalog = String.join("\n", catalogLines);
        String section = schemaKnowledge.getSchemaPromptSection("carclaimsDetails");

        assertTrue(catalog.contains("accidentCity | Ort"));
        assertTrue(section.contains("Step-specific hints (carclaimsDetails)"));
        assertFalse(catalog.contains("licensePlate |"));
    }

    @Test
    void stepScopedCatalogFiltersPolicyholderFields() {
        List<String> catalogLines = schemaKnowledge.filteredCatalogLinesForStep("insurance-holder-a");
        String catalog = String.join("\n", catalogLines);
        String section = schemaKnowledge.getSchemaPromptSection("insurance-holder-a");

        assertTrue(catalog.contains("licensePlate |"));
        assertFalse(catalog.contains("accidentCity | Ort"));
        assertFalse(catalog.contains("damageType |"));
        assertTrue(section.contains("licensePlate |"));
    }

    @Test
    void stepScopedPromptStaysWithinSizeLimit() {
        assertTrue(schemaKnowledge.getSchemaPromptSection("carclaimsDetails").length() < MAX_PROMPT_CHARS);
        assertTrue(schemaKnowledge.getSchemaPromptSection("driver-a").length() < MAX_PROMPT_CHARS);
    }

    @Test
    void unknownStepKeyFallsBackToFullCatalog() {
        String full = schemaKnowledge.getSchemaPromptSection(null);
        String unknown = schemaKnowledge.getSchemaPromptSection("nonexistent-step");

        assertTrue(full.contains("licensePlate |"));
        assertTrue(unknown.contains("licensePlate |"));
        assertFalse(full.contains("German field synonyms"));
        assertFalse(unknown.contains("German field synonyms"));
    }
}
