package me.owlaukka.api.exceptionmappers;

import jakarta.annotation.Priority;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import me.owlaukka.model.Error;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
@Priority(Integer.MAX_VALUE)
public class GlobalExceptionMapper implements ExceptionMapper<RuntimeException> {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionMapper.class);

    @Override
    public Response toResponse(RuntimeException exception) {
        logger.error("Unhandled runtime exception: {}", exception.getMessage(), exception);
        var error = new Error().code(Response.Status.INTERNAL_SERVER_ERROR.name()).message("Something went wrong");

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}

