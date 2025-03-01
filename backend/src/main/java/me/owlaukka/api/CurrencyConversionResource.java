package me.owlaukka.api;

import io.smallrye.faulttolerance.api.RateLimit;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import me.owlaukka.currencyconversion.CurrencyConversionService;
import me.owlaukka.model.ConversionResponse;
import me.owlaukka.model.Error;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

@ApplicationScoped
public class CurrencyConversionResource implements ConversionApi {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyConversionResource.class);

    @Inject
    CurrencyConversionService currencyConversionService;

    @Override
    @RateLimit // Default rate limit is 100 requests per second
    public Response convertCurrency(
            String sourceCurrency,
            String targetCurrency,
            String amount
    ) {
        logger.info("Currency conversion request received: {} to {}, amount: {}", sourceCurrency, targetCurrency, amount);
        var parsedAmount = new BigDecimal(amount);

        if (parsedAmount.compareTo(BigDecimal.ZERO) <= 0) {
            logger.warn("Invalid amount requested for conversion: {} (must be positive)", amount);
            var error = new Error()
                    .code("VALIDATION_ERROR")
                    .message("Amount must be positive");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        var conversion = currencyConversionService.convert(sourceCurrency, targetCurrency, parsedAmount);

        ConversionResponse response = new ConversionResponse()
                .convertedAmount(conversion.convertedAmount())
                .date(conversion.date());

        return Response.ok(response).build();
    }
}
