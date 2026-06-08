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
        assertTrue(section.contains("policyholder.vehicleReg | Kennzeichen"));
        assertTrue(section.contains("vehicleDriver.damageCausedBy | Auswahl der Schadenursache"));
        assertTrue(section.contains("policyholder.personalInformation.firstName | Vorname"));
    }

    @Test
    void catalogIncludesEnumValues() {
        String section = schemaKnowledge.getSchemaPromptSection();

        assertTrue(section.contains("hasVehicleDamage"));
        assertTrue(section.contains("enum: not_specified, false, true"));
        assertTrue(section.contains("enum: Auffahren, Rangieren/Parken"));
    }

    @Test
    void binaryAndImageFieldsAreExcluded() {
        String section = schemaKnowledge.getSchemaPromptSection();

        assertFalse(section.contains("damagedCarImages"));
        assertFalse(section.contains("damagedWindowImages"));
        assertFalse(section.contains("certificateForWildlife"));
    }

    @Test
    void promptSectionIncludesDomainContextAndVoiceHints() {
        String section = schemaKnowledge.getSchemaPromptSection();

        assertTrue(section.contains("Domain context:"));
        assertTrue(section.contains("FRIDA Schaden API"));
        assertTrue(section.contains("Voice mapping hints:"));
        assertTrue(section.contains("licensePlateNumber"));
    }

    @Test
    void stepPromptIncludesSynonymsAndExamples() {
        String section = schemaKnowledge.getSchemaPromptSection("carclaimsDetails");

        assertTrue(section.contains("German field synonyms"));
        assertTrue(section.contains("Kennzeichen, Nummernschild"));
        assertTrue(section.contains("Few-shot extraction examples"));
        assertTrue(section.contains("[Music]"));
    }

    @Test
    void promptSectionStaysWithinSizeLimit() {
        assertTrue(schemaKnowledge.getSchemaPromptSection().length() < MAX_PROMPT_CHARS);
    }

    @Test
    void stepScopedCatalogFiltersToAccidentFieldsOnly() {
        String section = schemaKnowledge.getSchemaPromptSection("carclaimsDetails");

        assertTrue(section.contains("accidentCity | Ort"));
        assertTrue(section.contains("Step-specific hints (carclaimsDetails)"));
        assertFalse(section.contains("policyholder.vehicleReg | Kennzeichen"));
        assertFalse(section.contains("otherPolicyholder.vehicleReg | Kennzeichen"));
    }

    @Test
    void stepScopedCatalogFiltersPolicyholderFields() {
        List<String> catalogLines = schemaKnowledge.filteredCatalogLinesForStep("insurance-holder-a");
        String catalog = String.join("\n", catalogLines);
        String section = schemaKnowledge.getSchemaPromptSection("insurance-holder-a");

        assertTrue(catalog.contains("policyholder.vehicleReg | Kennzeichen"));
        assertFalse(catalog.contains("accidentCity | Ort"));
        assertFalse(catalog.contains("vehicleDriver.damageCausedBy"));
        assertTrue(section.contains("policyholder.vehicleReg | Kennzeichen"));
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

        assertTrue(full.contains("policyholder.vehicleReg | Kennzeichen"));
        assertTrue(unknown.contains("policyholder.vehicleReg | Kennzeichen"));
        assertFalse(full.contains("German field synonyms"));
        assertFalse(unknown.contains("German field synonyms"));
    }
}
