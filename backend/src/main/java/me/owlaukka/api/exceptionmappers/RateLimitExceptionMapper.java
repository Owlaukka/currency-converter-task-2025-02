package me.owlaukka.api.exceptionmappers;

import io.smallrye.faulttolerance.api.RateLimitException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import me.owlaukka.model.Error;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class RateLimitExceptionMapper implements ExceptionMapper<RateLimitException> {
    private static final Logger logger = LoggerFactory.getLogger(RateLimitExceptionMapper.class);

    @Override
    public Response toResponse(RateLimitException exception) {
        logger.info("Rate limit exceeded. Retry after: {} ms", exception.getRetryAfterMillis());

        Error error = new Error()
                .code(Response.Status.TOO_MANY_REQUESTS.name())
                .message("Too many requests. Try again in about " + exception.getRetryAfterMillis() / 1000 + " seconds");
        return Response.status(Response.Status.TOO_MANY_REQUESTS)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
