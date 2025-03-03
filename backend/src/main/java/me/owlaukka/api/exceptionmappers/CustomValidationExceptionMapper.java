package me.owlaukka.api.exceptionmappers;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import me.owlaukka.currencyconversion.CustomValidationException;
import me.owlaukka.model.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class CustomValidationExceptionMapper implements ExceptionMapper<CustomValidationException> {
    private static final Logger logger = LoggerFactory.getLogger(CustomValidationExceptionMapper.class);

    @Override
    public Response toResponse(CustomValidationException e) {
        logger.warn("Validation error: {} - Fields: {}", e.getMessage(), e.getFields());
        ValidationError error = new ValidationError()
                .fields(e.getFields())
                .message(e.getMessage());
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
