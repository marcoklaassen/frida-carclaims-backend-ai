package click.klaassen.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
class VoiceExtractionPromptTest {

    @Inject
    ClaimsFieldExtractor claimsFieldExtractor;

    @Inject
    ClaimsSchemaKnowledge schemaKnowledge;

    @Test
    void extractionRulesRequireEmptyJsonForNoiseTranscripts() {
        String prompt = claimsFieldExtractor.systemPromptForStep("carclaimsDetails");

        assertTrue(prompt.contains("return {}"));
        assertTrue(prompt.contains("[Music]"));
        assertTrue(prompt.contains("do NOT copy unmentioned fields"));
    }

    @Test
    void extractionRulesEmphasizeGermanMapping() {
        String prompt = claimsFieldExtractor.systemPromptForStep(null);

        assertTrue(prompt.contains("transcript is in German"));
        assertTrue(prompt.contains("Map spoken German"));
    }

    @Test
    void stepPromptFocusesOnStepFields() {
        String full = claimsFieldExtractor.systemPromptForStep(null);
        String accident = claimsFieldExtractor.systemPromptForStep("carclaimsDetails");

        assertTrue(accident.contains("Step-specific hints (carclaimsDetails)"));
        assertTrue(accident.contains("German field synonyms"));
        assertFalse(full.contains("German field synonyms"));
        assertTrue(accident.contains("accidentCity | Ort"));
        assertFalse(accident.contains("policyholder.vehicleReg | Kennzeichen"));
    }

    @Test
    void driverStepIncludesDamageCauseEnum() {
        String section = schemaKnowledge.getSchemaPromptSection("driver-a");

        assertTrue(section.contains("vehicleDriver.damageCausedBy"));
        assertTrue(section.contains("enum: Auffahren, Rangieren/Parken"));
    }

    @Test
    void witnessStepIncludesWitnessArrayPaths() {
        String section = schemaKnowledge.getSchemaPromptSection("witness");

        assertTrue(section.contains("witnessExists"));
        assertTrue(section.contains("witnessCount"));
        assertTrue(section.contains("witness[].personalInformation"));
    }
}
