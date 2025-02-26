package me.owlaukka.api.exceptions.exceptionmappers;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import me.owlaukka.model.Error;
import me.owlaukka.rates.exceptions.ExchangeRateIntegrationException;

@Provider
public class ExchangeRateIntegrationExceptionMapper implements ExceptionMapper<ExchangeRateIntegrationException> {
    @Override
    public Response toResponse(ExchangeRateIntegrationException e) {
        Error error = new Error()
                .code(Response.Status.SERVICE_UNAVAILABLE.name())
                .message("Service temporarily unavailable");
        return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
