package click.klaassen.api;

import click.klaassen.service.VoiceExtractionService;
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

@Path("/api/voice")
public class VoiceExtractionResource {

    @Inject
    VoiceExtractionService voiceExtractionService;

    @POST
    @Path("/extract")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public VoiceExtractionResponse extract(
            @RestForm("audio") FileUpload audio,
            @RestForm("currentState") String currentState,
            @RestForm("language") String language) throws IOException {

        if (audio == null || audio.uploadedFile() == null) {
            throw new jakarta.ws.rs.BadRequestException("Missing required audio part");
        }

        byte[] audioBytes = Files.readAllBytes(audio.uploadedFile());
        if (audioBytes.length == 0) {
            throw new jakarta.ws.rs.BadRequestException("Audio file is empty");
        }

        String mimeType = audio.contentType() != null ? audio.contentType() : "application/octet-stream";
        return voiceExtractionService.extract(audioBytes, mimeType, currentState, language);
    }
}
