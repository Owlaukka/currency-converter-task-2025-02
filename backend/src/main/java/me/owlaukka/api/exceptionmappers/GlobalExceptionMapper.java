package me.owlaukka.api.exceptionmappers;

import jakarta.annotation.Priority;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import me.owlaukka.model.Error;

@Provider
@Priority(Integer.MAX_VALUE)
public class GlobalExceptionMapper implements ExceptionMapper<RuntimeException> {
    @Override
    public Response toResponse(RuntimeException exception) {
        System.out.println("exception.getClass().getName() = " + exception.getClass().getName());
        System.out.println("exception.getMessage() = " + exception.getMessage());
        var error = new Error().code(Response.Status.INTERNAL_SERVER_ERROR.name()).message("Something went wrong");

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}

