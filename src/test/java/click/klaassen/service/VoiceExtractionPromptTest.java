package click.klaassen.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
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
    void extractionRulesDescribePriorityBehavior() {
        String prompt = claimsFieldExtractor.systemPromptForStep(null);

        assertTrue(prompt.contains("priority field first"));
        assertFalse(prompt.contains("extract ONLY fields from that catalog"));
    }

    @Test
    void stepPromptFocusesOnStepFields() {
        String full = claimsFieldExtractor.systemPromptForStep(null);
        String accident = claimsFieldExtractor.systemPromptForStep("carclaimsDetails");

        assertTrue(accident.contains("Step-specific hints (carclaimsDetails)"));
        assertTrue(accident.contains("German field synonyms"));
        assertFalse(full.contains("German field synonyms"));
        assertTrue(accident.contains("accidentCity | Ort"));
        assertFalse(accident.contains("licensePlate | Kennzeichen"));
    }

    @Test
    void driverStepIncludesDamageCauseEnum() {
        String section = schemaKnowledge.getSchemaPromptSection("driver-a");

        assertTrue(section.contains("Priority fields for this step"));
        assertTrue(section.contains("damageType |"));
        assertTrue(section.contains("enum: Auffahren, Rangieren/ Parken"));
    }

    @Test
    void fixEmailInsertsMissingAtSymbol() {
        assertEquals("muster.frau@gmail.com", claimsFieldExtractor.fixEmail("muster.frau.gmail.com"));
        assertEquals("max@web.de", claimsFieldExtractor.fixEmail("max.web.de"));
        assertEquals("test@gmx.net", claimsFieldExtractor.fixEmail("test.gmx.net"));
        assertEquals("user@t-online.de", claimsFieldExtractor.fixEmail("user.t-online.de"));
        assertEquals("info@outlook.com", claimsFieldExtractor.fixEmail("info.outlook.com"));
    }

    @Test
    void fixEmailPreservesValidAddresses() {
        assertEquals("valid@gmail.com", claimsFieldExtractor.fixEmail("valid@gmail.com"));
        assertNull(claimsFieldExtractor.fixEmail(null));
    }

    @Test
    void witnessStepIncludesWitnessArrayPaths() {
        String section = schemaKnowledge.getSchemaPromptSection("witness");

        assertTrue(section.contains("hasWitnesses"));
        assertTrue(section.contains("witnessesCount"));
        assertTrue(section.contains("witnesses[]."));
    }
}
