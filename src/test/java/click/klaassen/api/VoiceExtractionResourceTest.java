package click.klaassen.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

import click.klaassen.claims.model.Claimsdata;
import click.klaassen.service.VoiceExtractionService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

@QuarkusTest
class VoiceExtractionResourceTest {

    @InjectMock
    VoiceExtractionService voiceExtractionService;

    @Test
    void extractReturnsTranscriptAndClaimsData() {
        Claimsdata claimsData = new Claimsdata();
        claimsData.setAccidentCity("Bedburg");

        when(voiceExtractionService.extract(any(byte[].class), anyString(), isNull(), isNull()))
                .thenReturn(new VoiceExtractionResponse("Unfall in Bedburg", claimsData));

        given()
                .multiPart("audio", "test.webm", "audio-content".getBytes(), "audio/webm")
                .when()
                .post("/api/voice/extract")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("transcript", equalTo("Unfall in Bedburg"))
                .body("claimsData.accidentCity", equalTo("Bedburg"));
    }

    @Test
    void extractMissingAudioReturns400() {
        given()
                .multiPart("currentState", "{}")
                .when()
                .post("/api/voice/extract")
                .then()
                .statusCode(400)
                .body("error", equalTo("Missing required audio part"));
    }

    @Test
    void extractEmptyAudioReturns400() {
        given()
                .multiPart("audio", "empty.webm", new byte[0], "audio/webm")
                .when()
                .post("/api/voice/extract")
                .then()
                .statusCode(400)
                .body("error", equalTo("Audio file is empty"));
    }
}
