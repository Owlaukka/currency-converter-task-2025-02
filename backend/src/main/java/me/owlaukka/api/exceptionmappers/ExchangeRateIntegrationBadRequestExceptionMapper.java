package me.owlaukka.api.exceptionmappers;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import me.owlaukka.model.Error;
import me.owlaukka.rates.exceptions.ExchangeRateIntegrationBadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class ExchangeRateIntegrationBadRequestExceptionMapper implements ExceptionMapper<ExchangeRateIntegrationBadRequestException> {
    private static final Logger logger = LoggerFactory.getLogger(ExchangeRateIntegrationBadRequestExceptionMapper.class);

    @Override
    public Response toResponse(ExchangeRateIntegrationBadRequestException e) {
        logger.warn("Bad request to exchange rate integration: {}", e.getMessage());
        Error error = new Error()
                .code(Response.Status.BAD_REQUEST.name())
                .message("Bad request to exchange rate integration");
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
