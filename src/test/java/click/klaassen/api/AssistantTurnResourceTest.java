package click.klaassen.api;

import static io.restassured.RestAssured.given;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
class AssistantTurnResourceTest {

    @Test
    void turnWithoutCurrentStateReturns400() {
        given()
                .multiPart("conversationHistory", "[]")
                .when().post("/api/assistant/turn")
                .then().statusCode(400);
    }

    @Test
    void audioEndpointWithUnknownIdReturns404() {
        given()
                .when().get("/api/assistant/audio/nonexistent-id")
                .then().statusCode(404);
    }
}
