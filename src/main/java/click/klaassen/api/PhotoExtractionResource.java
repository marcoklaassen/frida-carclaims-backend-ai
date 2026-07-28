package click.klaassen.api;

import click.klaassen.service.PhotoExtractionService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.io.IOException;
import java.nio.file.Files;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

@Path("/api/photo")
public class PhotoExtractionResource {

    @Inject
    PhotoExtractionService photoExtractionService;

    @POST
    @Path("/extract")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public PhotoExtractionResponse extract(
            @RestForm("image") FileUpload image,
            @RestForm("currentState") String currentState,
            @RestForm("stepKey") String stepKey) throws IOException {

        if (image == null || image.uploadedFile() == null) {
            throw new jakarta.ws.rs.BadRequestException("Missing required image part");
        }

        byte[] imageBytes = Files.readAllBytes(image.uploadedFile());
        if (imageBytes.length == 0) {
            throw new jakarta.ws.rs.BadRequestException("Image file is empty");
        }

        String mimeType = image.contentType() != null ? image.contentType() : "image/jpeg";
        return photoExtractionService.extract(imageBytes, mimeType, currentState, stepKey);
    }
}
