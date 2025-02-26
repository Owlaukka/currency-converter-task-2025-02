package me.owlaukka.api.exceptions.exceptionmappers;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import me.owlaukka.model.Error;
import me.owlaukka.rates.exceptions.ExchangeRateIntegrationBadRequestException;

@Provider
public class ExchangeRateIntegrationBadRequestExceptionMapper implements ExceptionMapper<ExchangeRateIntegrationBadRequestException> {
    @Override
    public Response toResponse(ExchangeRateIntegrationBadRequestException e) {
        Error error = new Error()
                .code(Response.Status.BAD_REQUEST.name())
                .message("Bad request to exchange rate integration");
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
