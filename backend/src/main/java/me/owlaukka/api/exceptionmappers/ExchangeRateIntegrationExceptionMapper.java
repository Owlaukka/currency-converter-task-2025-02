package me.owlaukka.api.exceptionmappers;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import me.owlaukka.model.Error;
import me.owlaukka.rates.exceptions.ExchangeRateIntegrationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class ExchangeRateIntegrationExceptionMapper implements ExceptionMapper<ExchangeRateIntegrationException> {
    private static final Logger logger = LoggerFactory.getLogger(ExchangeRateIntegrationExceptionMapper.class);

    @Override
    public Response toResponse(ExchangeRateIntegrationException e) {
        logger.error("Exchange rate integration error: {}", e.getMessage(), e);
        Error error = new Error()
                .code(Response.Status.SERVICE_UNAVAILABLE.name())
                .message("Service temporarily unavailable");
        return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
