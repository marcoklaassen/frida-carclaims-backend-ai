package click.klaassen.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import click.klaassen.claims.model.Claimsdata;
import click.klaassen.claims.model.Person;
import click.klaassen.claims.model.Policyholder;
import click.klaassen.claims.model.Witness;
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
    void mergeNestedPersonFields() {
        Person currentPerson = new Person();
        currentPerson.setFirstName("Alice");

        Policyholder current = new Policyholder();
        current.setPersonalInformation(currentPerson);

        Person extractedPerson = new Person();
        extractedPerson.setLastName("Johnson");

        Policyholder extracted = new Policyholder();
        extracted.setPersonalInformation(extractedPerson);

        Claimsdata currentClaims = new Claimsdata();
        currentClaims.setPolicyholder(current);

        Claimsdata extractedClaims = new Claimsdata();
        extractedClaims.setPolicyholder(extracted);

        Claimsdata merged = merger.merge(currentClaims, extractedClaims);

        assertNotNull(merged.getPolicyholder());
        assertEquals("Alice", merged.getPolicyholder().getPersonalInformation().getFirstName());
        assertEquals("Johnson", merged.getPolicyholder().getPersonalInformation().getLastName());
    }

    @Test
    void mergeAppendsWitnesses() {
        Witness existing = new Witness();
        Person existingPerson = new Person();
        existingPerson.setFirstName("Max");
        existing.setPersonalInformation(existingPerson);

        Witness added = new Witness();
        Person addedPerson = new Person();
        addedPerson.setFirstName("Anna");
        added.setPersonalInformation(addedPerson);

        Claimsdata current = new Claimsdata();
        current.setWitness(new ArrayList<>(List.of(existing)));

        Claimsdata extracted = new Claimsdata();
        extracted.setWitness(List.of(added));

        Claimsdata merged = merger.merge(current, extracted);

        assertEquals(2, merged.getWitness().size());
        assertEquals("Max", merged.getWitness().get(0).getPersonalInformation().getFirstName());
        assertEquals("Anna", merged.getWitness().get(1).getPersonalInformation().getFirstName());
    }

    @Test
    void mergeTriStatePrefersExtracted() {
        Claimsdata current = new Claimsdata();
        current.setInjuredPerson(TriState.FALSE);

        Claimsdata extracted = new Claimsdata();
        extracted.setInjuredPerson(TriState.TRUE);

        Claimsdata merged = merger.merge(current, extracted);

        assertEquals(TriState.TRUE, merged.getInjuredPerson());
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
