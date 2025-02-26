package me.owlaukka.api.exceptions.exceptionmappers;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import me.owlaukka.model.Error;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<RuntimeException> {
    @Override
    public Response toResponse(RuntimeException exception) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new Error().code(Response.Status.INTERNAL_SERVER_ERROR.name()).message("Something went wrong"))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}

