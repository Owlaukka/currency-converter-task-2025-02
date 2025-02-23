package me.owlaukka;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import me.owlaukka.api.ConversionApi;
import me.owlaukka.model.ConversionResponse;
import me.owlaukka.model.Error;

import java.time.LocalDate;

@ApplicationScoped
public class CurrencyConversionResource implements ConversionApi {

    @Override
    public Response convertCurrency(
        String sourceCurrency,
        String targetCurrency,
        Double amount
    ) {
        // TODO: Implement the actual currency conversion using swop.cx API
        // For now, return a mock response
        ConversionResponse response = new ConversionResponse()
            .convertedAmount(amount * 0.85) // Mock conversion rate
            .date(LocalDate.now());

        return Response.ok(response).build();
    }

    @Provider
    public static class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
        @Override
        public Response toResponse(ConstraintViolationException e) {
            Error error = new Error()
                .code("VALIDATION_ERROR")
                .message("Invalid input parameters: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(error)
                .build();
        }
    }
}
