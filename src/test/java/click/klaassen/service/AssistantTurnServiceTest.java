package click.klaassen.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import click.klaassen.claims.model.Claimsdata;
import click.klaassen.claims.model.enums.TriState;
import java.util.List;
import org.junit.jupiter.api.Test;

class AssistantTurnServiceTest {

    @Test
    void enrichTranscriptWithContext() {
        String result = AssistantTurnService.enrichTranscriptWithContext(
                "Ja, das war am 15. März.",
                "Wann ist der Unfall passiert?");
        assertNotNull(result);
        assertEquals(
                "Der Assistent hat gefragt: \"Wann ist der Unfall passiert?\"\nDie Antwort war: \"Ja, das war am 15. März.\"",
                result);
    }

    @Test
    void enrichTranscriptWithNullQuestion() {
        String result = AssistantTurnService.enrichTranscriptWithContext(
                "Hallo, ich hatte einen Unfall.", null);
        assertEquals("Hallo, ich hatte einen Unfall.", result);
    }

    @Test
    void enrichTranscriptWithBlankQuestion() {
        String result = AssistantTurnService.enrichTranscriptWithContext(
                "Hallo", "  ");
        assertEquals("Hallo", result);
    }

    @Test
    void swapPartyFields_swapsAtoB() {
        AssistantTurnService service = new AssistantTurnService();
        Claimsdata data = new Claimsdata();

        // Set party A fields
        data.setInsuranceHolderSalutation("Herr");
        data.setInsuranceHolderName("Max");
        data.setInsuranceHolderSurName("Mustermann");
        data.setCarBrand("BMW");
        data.setCarModel("3er");
        data.setLicensePlate("B-AB 123");
        data.setVatDeduction(TriState.TRUE);
        data.setAllRiskInsurance(TriState.FALSE);
        data.setDriverSalutation("Herr");
        data.setDriverName("Max");
        data.setDriverDamagedParts(List.of("Motorhaube", "Kuehlergrill"));
        data.setDamageDescription("Frontalschaden");
        data.setVehicleOperational(TriState.TRUE);

        // Swap
        Claimsdata result = service.swapPartyFields(data);

        // Verify A fields are now in B
        assertEquals("Herr", result.getOtherInsuranceHolderSalutation());
        assertEquals("Max", result.getOtherInsuranceHolderName());
        assertEquals("Mustermann", result.getOtherInsuranceHolderSurName());
        assertEquals("BMW", result.getOtherCarBrand());
        assertEquals("3er", result.getOtherCarModel());
        assertEquals("B-AB 123", result.getOtherLicensePlate());
        assertEquals(TriState.TRUE, result.getOtherVatDeduction());
        assertEquals(TriState.FALSE, result.getOtherAllRiskInsurance());
        assertEquals("Herr", result.getOtherDriverSalutation());
        assertEquals("Max", result.getOtherDriverName());
        assertEquals(List.of("Motorhaube", "Kuehlergrill"), result.getOtherDriverDamagedParts());
        assertEquals("Frontalschaden", result.getOtherDamageDescription());
        assertEquals(TriState.TRUE, result.getOtherVehicleOperational());

        // Verify A fields are now null/empty
        assertNull(result.getInsuranceHolderSalutation());
        assertNull(result.getInsuranceHolderName());
        assertNull(result.getInsuranceHolderSurName());
        assertNull(result.getCarBrand());
        assertNull(result.getCarModel());
        assertNull(result.getLicensePlate());
        assertNull(result.getVatDeduction());
        assertNull(result.getAllRiskInsurance());
        assertNull(result.getDriverSalutation());
        assertNull(result.getDriverName());
        assertNull(result.getDriverDamagedParts());
        assertNull(result.getDamageDescription());
        assertNull(result.getVehicleOperational());
    }
}
