package click.klaassen.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
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
    void promptSectionStaysWithinSizeLimit() {
        assertTrue(schemaKnowledge.getSchemaPromptSection().length() < MAX_PROMPT_CHARS);
    }
}
