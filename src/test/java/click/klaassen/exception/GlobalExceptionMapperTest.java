package click.klaassen.exception;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.Response;
import java.util.concurrent.CompletionException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionMapperTest {

    private final GlobalExceptionMapper mapper = new GlobalExceptionMapper();

    @Test
    void mapsBadRequestException() {
        Response response = mapper.toResponse(new BadRequestException("Missing audio"));
        assertEquals(400, response.getStatus());
    }

    @Test
    void mapsUpstreamAiException() {
        Response response = mapper.toResponse(new UpstreamAiException("LLM failed", new RuntimeException("timeout")));
        assertEquals(502, response.getStatus());
    }

    @Test
    void unwrapsUpstreamAiExceptionFromCompletionException() {
        Response response = mapper.toResponse(new CompletionException(
                new UpstreamAiException("Claims extraction failed", new RuntimeException("model error"))));
        assertEquals(502, response.getStatus());
    }

    @Test
    void mapsUnknownExceptionTo500() {
        Response response = mapper.toResponse(new IllegalStateException("unexpected"));
        assertEquals(500, response.getStatus());
    }
}
