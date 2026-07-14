package click.klaassen.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import click.klaassen.claims.model.Claimsdata;
import click.klaassen.claims.model.WitnessDetails;
import click.klaassen.claims.model.enums.TriState;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClaimsDataMergerTest {

    private ClaimsDataMerger merger;

    @BeforeEach
    void setUp() {
        merger = new ClaimsDataMerger();
    }

    @Test
    void mergeScalarFieldsFromExtracted() {
        Claimsdata current = new Claimsdata();
        current.setAccidentCity("Köln");

        Claimsdata extracted = new Claimsdata();
        extracted.setAccidentDate("2019-08-24");

        Claimsdata merged = merger.merge(current, extracted);

        assertEquals("Köln", merged.getAccidentCity());
        assertEquals("2019-08-24", merged.getAccidentDate());
    }

    @Test
    void mergeFlatInsuranceHolderFields() {
        Claimsdata current = new Claimsdata();
        current.setInsuranceHolderSurName("Alice");

        Claimsdata extracted = new Claimsdata();
        extracted.setInsuranceHolderName("Johnson");

        Claimsdata merged = merger.merge(current, extracted);

        assertEquals("Alice", merged.getInsuranceHolderSurName());
        assertEquals("Johnson", merged.getInsuranceHolderName());
    }

    @Test
    void mergeAppendsWitnesses() {
        WitnessDetails existing = new WitnessDetails();
        existing.setSurName("Max");

        WitnessDetails added = new WitnessDetails();
        added.setSurName("Anna");

        Claimsdata current = new Claimsdata();
        current.setWitnesses(new ArrayList<>(List.of(existing)));

        Claimsdata extracted = new Claimsdata();
        extracted.setWitnesses(List.of(added));

        Claimsdata merged = merger.merge(current, extracted);

        assertEquals(2, merged.getWitnesses().size());
        assertEquals("Max", merged.getWitnesses().get(0).getSurName());
        assertEquals("Anna", merged.getWitnesses().get(1).getSurName());
    }

    @Test
    void mergeTriStatePrefersExtracted() {
        Claimsdata current = new Claimsdata();
        current.setHasInjured(TriState.FALSE);

        Claimsdata extracted = new Claimsdata();
        extracted.setHasInjured(TriState.TRUE);

        Claimsdata merged = merger.merge(current, extracted);

        assertEquals(TriState.TRUE, merged.getHasInjured());
    }

    @Test
    void mergeNullExtractedReturnsCurrent() {
        Claimsdata current = new Claimsdata();
        current.setAccidentCity("Bedburg");

        Claimsdata merged = merger.merge(current, null);

        assertEquals("Bedburg", merged.getAccidentCity());
    }

    @Test
    void mergeNullCurrentReturnsExtracted() {
        Claimsdata extracted = new Claimsdata();
        extracted.setAccidentCity("Bedburg");

        Claimsdata merged = merger.merge(null, extracted);

        assertEquals("Bedburg", merged.getAccidentCity());
    }

    @Test
    void mergeBothNullReturnsNull() {
        assertNull(merger.merge(null, null));
    }
}
