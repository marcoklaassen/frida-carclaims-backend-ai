package click.klaassen.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.List;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ClaimsSchemaKnowledgeTest {

    private static final int MAX_PROMPT_CHARS = 24_000;

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
        String section = schemaKnowledge.getSchemaPromptSection("accident-info");

        assertTrue(section.contains("German field synonyms"));
        assertTrue(section.contains("Few-shot extraction examples"));
    }

    @Test
    void promptSectionStaysWithinSizeLimit() {
        assertTrue(schemaKnowledge.getSchemaPromptSection().length() < MAX_PROMPT_CHARS);
    }

    @Test
    void stepScopedCatalogFiltersToAccidentFieldsOnly() {
        List<String> catalogLines = schemaKnowledge.filteredCatalogLinesForStep("accident-location");
        String catalog = String.join("\n", catalogLines);
        String section = schemaKnowledge.getSchemaPromptSection("accident-location");

        assertTrue(catalog.contains("accidentCity | Ort"));
        assertTrue(section.contains("Step-specific hints (accident-location)"));
        assertFalse(catalog.contains("licensePlate |"));
    }

    @Test
    void stepScopedCatalogFiltersPolicyholderFields() {
        List<String> catalogLines = schemaKnowledge.filteredCatalogLinesForStep("vehicle-info-a");
        String catalog = String.join("\n", catalogLines);
        String section = schemaKnowledge.getSchemaPromptSection("vehicle-info-a");

        assertTrue(catalog.contains("licensePlate |"));
        assertFalse(catalog.contains("accidentCity | Ort"));
        assertFalse(catalog.contains("damageType |"));
        assertTrue(section.contains("licensePlate |"));
    }

    @Test
    void stepScopedPromptStaysWithinSizeLimit() {
        assertTrue(schemaKnowledge.getSchemaPromptSection("accident-info").length() < MAX_PROMPT_CHARS);
        assertTrue(schemaKnowledge.getSchemaPromptSection("driver-info-a").length() < MAX_PROMPT_CHARS);
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

    @Test
    void stepPromptContainsPriorityAndOtherFieldsSections() {
        String section = schemaKnowledge.getSchemaPromptSection("driver-info-a");

        assertTrue(section.contains("Priority fields for this step"));
        assertTrue(section.contains("Other available fields"));
    }

    @Test
    void stepPromptOtherFieldsIncludesNonStepFields() {
        String section = schemaKnowledge.getSchemaPromptSection("driver-info-a");

        assertTrue(section.contains("accidentCity"));
        assertTrue(section.contains("licensePlate"));
    }

    @Test
    void stepPromptPriorityFieldsContainStepFields() {
        String section = schemaKnowledge.getSchemaPromptSection("driver-info-a");
        String prioritySection = section.substring(
                section.indexOf("Priority fields"),
                section.indexOf("Other available fields"));

        assertTrue(prioritySection.contains("driver"));
        assertFalse(prioritySection.contains("damageType |"));
        assertFalse(prioritySection.contains("accidentCity |"));
    }
}
