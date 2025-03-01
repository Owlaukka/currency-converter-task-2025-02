package me.owlaukka.api;

import io.smallrye.faulttolerance.api.RateLimit;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import me.owlaukka.currencyconversion.CurrencyConversionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class CurrenciesResource implements CurrenciesApi {

    private static final Logger logger = LoggerFactory.getLogger(CurrenciesResource.class);

    @Inject
    CurrencyConversionService currencyConversionService;

    @Override
    @RateLimit // Default rate limit is 100 requests per second
    public Response getSupportedCurrencies() {
        logger.info("Request received for supported currencies");
        var currencies = currencyConversionService.getAllSupportedCurrencies();

        logger.debug("Returning {} supported currencies", currencies.size());

        return Response.ok(currencies).build();
    }
}
