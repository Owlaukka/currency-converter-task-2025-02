package me.owlaukka.api;

import io.smallrye.faulttolerance.api.RateLimit;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import me.owlaukka.currencyconversion.CurrencyConversionService;

@ApplicationScoped
public class CurrenciesResource implements CurrenciesApi {

    @Inject
    CurrencyConversionService currencyConversionService;

    @Override
    @RateLimit // Default rate limit is 100 requests per second
    public Response getSupportedCurrencies() {
        return Response.ok(currencyConversionService.getAllSupportedCurrencies()).build();
    }
}
