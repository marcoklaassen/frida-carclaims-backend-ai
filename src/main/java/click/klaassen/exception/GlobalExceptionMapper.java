package click.klaassen.exception;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Map;
import org.jboss.logging.Logger;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    private static final Logger LOG = Logger.getLogger(GlobalExceptionMapper.class);

    @Override
    public Response toResponse(Exception exception) {
        LOG.error("Request failed: " + exception.getClass().getName() + ": " + exception.getMessage(), exception);

        BadRequestException badRequest = findInChain(exception, BadRequestException.class);
        if (badRequest != null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(Map.of("error", badRequest.getMessage()))
                    .build();
        }

        if (findInChain(exception, UpstreamAiException.class) != null) {
            return Response.status(502)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(Map.of("error", "Upstream AI service failed"))
                    .build();
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(Map.of("error", "Internal server error"))
                .build();
    }

    private static <T extends Throwable> T findInChain(Throwable throwable, Class<T> type) {
        Throwable current = throwable;
        while (current != null) {
            if (type.isInstance(current)) {
                return type.cast(current);
            }
            current = current.getCause();
        }
        return null;
    }
}
