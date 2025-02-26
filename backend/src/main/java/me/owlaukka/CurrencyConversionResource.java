package me.owlaukka;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import me.owlaukka.api.ConversionApi;
import me.owlaukka.currencyconversion.CurrencyConversionService;
import me.owlaukka.model.ConversionResponse;
import me.owlaukka.model.Error;

import java.math.BigDecimal;

@ApplicationScoped
public class CurrencyConversionResource implements ConversionApi {

    @Inject
    CurrencyConversionService currencyConversionService;

    @Override
    public Response convertCurrency(
        String sourceCurrency,
        String targetCurrency,
        BigDecimal amount
    ) {
        var conversion = currencyConversionService.convert(sourceCurrency, targetCurrency, amount);

        ConversionResponse response = new ConversionResponse()
                .convertedAmount(conversion.convertedAmount())
                .date(conversion.date());

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
