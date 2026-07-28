package click.klaassen.api;

import click.klaassen.service.AssistantTurnService;
import click.klaassen.service.TtsAudioCache;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

@Path("/api/assistant")
public class AssistantTurnResource {

    @Inject
    AssistantTurnService assistantTurnService;

    @Inject
    TtsAudioCache ttsAudioCache;

    @POST
    @Path("/turn")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response turn(
            @RestForm("audio") FileUpload audio,
            @RestForm("currentState") String currentState,
            @RestForm("conversationHistory") String conversationHistory,
            @RestForm("previousStepKey") String previousStepKey,
            @RestForm("previousQuestion") String previousQuestion) throws IOException {

        if (currentState == null || currentState.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Missing required currentState\"}")
                    .build();
        }

        byte[] audioBytes = null;
        String mimeType = null;
        if (audio != null && audio.uploadedFile() != null) {
            audioBytes = Files.readAllBytes(audio.uploadedFile());
            mimeType = audio.contentType() != null ? audio.contentType() : "audio/webm";
        }

        AssistantTurnResponse response = assistantTurnService.processTurn(
                audioBytes, mimeType, currentState,
                conversationHistory, previousStepKey, previousQuestion);

        return Response.ok(response).build();
    }

    @GET
    @Path("/audio/{id}")
    @Produces("audio/mpeg")
    public Response getAudio(@PathParam("id") String id) {
        Optional<byte[]> audio = ttsAudioCache.get(id);
        if (audio.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(audio.get())
                .header("Content-Type", "audio/mpeg")
                .header("Cache-Control", "no-cache")
                .build();
    }
}
