package click.klaassen.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.Optional;
import org.junit.jupiter.api.Test;

@QuarkusTest
class DemoCustomerDatabaseTest {

    @Inject
    DemoCustomerDatabase database;

    @Test
    void findByExactPlateReturnsCustomer() {
        Optional<DemoCustomer> result = database.findByLicensePlate("HH-AB 1234");
        assertTrue(result.isPresent());
        assertEquals("Mustermann", result.get().surName());
        assertEquals("BMW", result.get().carBrand());
        assertEquals("Allianz", result.get().insuranceCompany());
    }

    @Test
    void findByPlateIsCaseInsensitive() {
        Optional<DemoCustomer> result = database.findByLicensePlate("hh-ab 1234");
        assertTrue(result.isPresent());
        assertEquals("Mustermann", result.get().surName());
    }

    @Test
    void findByUnknownPlateReturnsEmpty() {
        Optional<DemoCustomer> result = database.findByLicensePlate("XX-ZZ 9999");
        assertTrue(result.isEmpty());
    }

    @Test
    void findByNullReturnsEmpty() {
        assertTrue(database.findByLicensePlate(null).isEmpty());
    }

    @Test
    void findByBlankReturnsEmpty() {
        assertTrue(database.findByLicensePlate("  ").isEmpty());
    }

    @Test
    void allFourDemoCustomersLoaded() {
        assertTrue(database.findByLicensePlate("HH-AB 1234").isPresent());
        assertTrue(database.findByLicensePlate("M-CD 5678").isPresent());
        assertTrue(database.findByLicensePlate("B-EF 9012").isPresent());
        assertTrue(database.findByLicensePlate("K-GH 3456").isPresent());
    }
}
