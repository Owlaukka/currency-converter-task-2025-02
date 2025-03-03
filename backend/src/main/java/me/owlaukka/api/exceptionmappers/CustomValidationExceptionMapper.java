package me.owlaukka.api.exceptionmappers;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import me.owlaukka.currencyconversion.CustomValidationException;
import me.owlaukka.model.ValidationError;

@Provider
public class CustomValidationExceptionMapper implements ExceptionMapper<CustomValidationException> {
    @Override
    public Response toResponse(CustomValidationException e) {
        ValidationError error = new ValidationError()
                .fields(e.getFields())
                .message(e.getMessage());
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
