package click.klaassen.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import click.klaassen.claims.model.Claimsdata;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class ClaimsJsonParserTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void parsePlainJson() throws Exception {
        ClaimsJsonParser.parse(objectMapper, "{\"accidentCity\":\"Bedburg\"}");
        assertEquals("Bedburg", ClaimsJsonParser.parse(objectMapper, "{\"accidentCity\":\"Bedburg\"}").getAccidentCity());
    }

    @Test
    void parseMarkdownWrappedJson() throws Exception {
        String response = """
                Here is the result:
                ```json
                {"accidentCity":"Köln"}
                ```
                """;
        assertEquals("Köln", ClaimsJsonParser.parse(objectMapper, response).getAccidentCity());
    }

    @Test
    void parseJsonAfterProse() throws Exception {
        String response = """
                Some reasoning text before the object.
                {"accidentDate":"2019-08-24"}
                """;
        assertEquals("2019-08-24", ClaimsJsonParser.parse(objectMapper, response).getAccidentDate());
    }

    @Test
    void throwsWhenNoJson() {
        assertThrows(IllegalArgumentException.class, () -> ClaimsJsonParser.extractJson("no json here"));
    }

    @Test
    void parseRemovesInvalidNotSpecifiedFromDamageCause() throws Exception {
        String json = """
                {
                  "policyholder": {
                    "personalInformation": { "firstName": "Marco", "lastName": "Klasen" }
                  },
                  "otherVehicleDriver": {
                    "damageCausedBy": "not_specified"
                  }
                }
                """;
        Claimsdata claims = ClaimsJsonParser.parse(objectMapper, json);
        assertEquals("Marco", claims.getPolicyholder().getPersonalInformation().getFirstName());
        assertEquals(null, claims.getOtherVehicleDriver());
    }

    @Test
    void parseKeepsNotSpecifiedForTriStateFields() throws Exception {
        String json = """
                {
                  "hasVehicleDamage": "not_specified",
                  "policyholder": {
                    "personalInformation": { "formOfAddress": "not_specified" }
                  }
                }
                """;
        Claimsdata claims = ClaimsJsonParser.parse(objectMapper, json);
        assertEquals("not_specified", claims.getHasVehicleDamage().getValue());
        assertEquals("not_specified", claims.getPolicyholder().getPersonalInformation().getFormOfAddress().getValue());
    }
}
