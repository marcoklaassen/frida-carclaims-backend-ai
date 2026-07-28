package click.klaassen.service;

import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import org.yaml.snakeyaml.Yaml;

@ApplicationScoped
@Startup
public class DemoCustomerDatabase {

    private static final Logger LOG = Logger.getLogger(DemoCustomerDatabase.class.getName());
    private static final String RESOURCE = "/frida/demo-customers.yaml";

    private Map<String, DemoCustomer> customersByPlate;

    @PostConstruct
    void init() {
        customersByPlate = loadCustomers();
        LOG.info("Loaded " + customersByPlate.size() + " demo customers");
    }

    public Optional<DemoCustomer> findByLicensePlate(String plate) {
        if (plate == null || plate.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(customersByPlate.get(normalizePlate(plate)));
    }

    static String normalizePlate(String plate) {
        return plate.toUpperCase().replaceAll("\\s+", " ").trim();
    }

    @SuppressWarnings("unchecked")
    private Map<String, DemoCustomer> loadCustomers() {
        Yaml yaml = new Yaml();
        try (InputStream in = getClass().getResourceAsStream(RESOURCE)) {
            if (in == null) {
                throw new IOException("Resource not found: " + RESOURCE);
            }
            Map<String, Object> root = yaml.load(in);
            List<Map<String, Object>> entries = (List<Map<String, Object>>) root.get("customers");
            if (entries == null) {
                return Map.of();
            }
            Map<String, DemoCustomer> result = new LinkedHashMap<>();
            for (Map<String, Object> entry : entries) {
                DemoCustomer customer = new DemoCustomer(
                        str(entry, "licensePlate"),
                        str(entry, "salutation"),
                        str(entry, "name"),
                        str(entry, "surName"),
                        str(entry, "streetName"),
                        str(entry, "houseNumber"),
                        str(entry, "postalCode"),
                        str(entry, "city"),
                        str(entry, "telephone"),
                        str(entry, "email"),
                        str(entry, "carBrand"),
                        str(entry, "carModel"),
                        str(entry, "insuranceCompany"),
                        str(entry, "insuranceNumber"),
                        str(entry, "chassisNumber"),
                        bool(entry, "allRiskInsurance"));
                result.put(normalizePlate(customer.licensePlate()), customer);
            }
            return Map.copyOf(result);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load " + RESOURCE, e);
        }
    }

    private static String str(Map<String, Object> map, String key) {
        Object val = map.get(key);
        return val == null ? null : String.valueOf(val);
    }

    private static boolean bool(Map<String, Object> map, String key) {
        Object val = map.get(key);
        return val instanceof Boolean b ? b : Boolean.parseBoolean(String.valueOf(val));
    }
}
