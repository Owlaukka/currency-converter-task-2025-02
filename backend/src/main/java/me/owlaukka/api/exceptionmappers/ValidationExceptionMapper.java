package me.owlaukka.api.exceptionmappers;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import me.owlaukka.model.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
    private static final Logger logger = LoggerFactory.getLogger(ValidationExceptionMapper.class);

    @Override
    public Response toResponse(ConstraintViolationException e) {
        var invalidFields = e.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath().toString())
                .collect(Collectors.toSet());

        logger.warn("Validation error: {} - Fields: {}", e.getMessage(), invalidFields);

        ValidationError error = new ValidationError()
                .fields(invalidFields.stream().toList())
                .message("Invalid input parameters: " + e.getMessage());
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
