package click.klaassen.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

import click.klaassen.claims.model.Claimsdata;
import click.klaassen.service.PhotoExtractionService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

@QuarkusTest
class PhotoExtractionResourceTest {

    @InjectMock
    PhotoExtractionService photoExtractionService;

    @Test
    void extractReturnsDescriptionAndClaimsData() {
        Claimsdata claimsData = new Claimsdata();
        claimsData.setLicensePlate("HH-AB 1234");
        claimsData.setCarBrand("BMW");

        when(photoExtractionService.extract(any(byte[].class), anyString(), isNull(), isNull()))
                .thenReturn(new PhotoExtractionResponse("Silver BMW with plate HH-AB 1234", claimsData));

        given()
                .multiPart("image", "test.jpg", "fake-image-bytes".getBytes(), "image/jpeg")
                .when()
                .post("/api/photo/extract")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("imageDescription", equalTo("Silver BMW with plate HH-AB 1234"))
                .body("claimsData.licensePlate", equalTo("HH-AB 1234"))
                .body("claimsData.carBrand", equalTo("BMW"));
    }

    @Test
    void extractMissingImageReturns400() {
        given()
                .multiPart("currentState", "{}")
                .when()
                .post("/api/photo/extract")
                .then()
                .statusCode(400)
                .body("error", equalTo("Missing required image part"));
    }

    @Test
    void extractEmptyImageReturns400() {
        given()
                .multiPart("image", "empty.jpg", new byte[0], "image/jpeg")
                .when()
                .post("/api/photo/extract")
                .then()
                .statusCode(400)
                .body("error", equalTo("Image file is empty"));
    }
}
