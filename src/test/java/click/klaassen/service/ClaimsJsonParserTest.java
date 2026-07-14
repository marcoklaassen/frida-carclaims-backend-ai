package click.klaassen.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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
    void parseRemovesInvalidNotSpecifiedFromDamageType() throws Exception {
        String json = """
                {
                  "insuranceHolderSurName": "Marco",
                  "insuranceHolderName": "Klasen",
                  "otherDamageType": "not_specified"
                }
                """;
        Claimsdata claims = ClaimsJsonParser.parse(objectMapper, json);
        assertEquals("Marco", claims.getInsuranceHolderSurName());
        assertNull(claims.getOtherDamageType());
    }

    @Test
    void parseKeepsNotSpecifiedForTriStateFields() throws Exception {
        String json = """
                {
                  "miscellaneousDamages": "not_specified",
                  "insuranceHolderSalutation": "not_specified"
                }
                """;
        Claimsdata claims = ClaimsJsonParser.parse(objectMapper, json);
        assertEquals("not_specified", claims.getMiscellaneousDamages().getValue());
        assertEquals("not_specified", claims.getInsuranceHolderSalutation());
    }
}
