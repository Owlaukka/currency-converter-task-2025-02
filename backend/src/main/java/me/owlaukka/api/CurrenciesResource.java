package me.owlaukka.api;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import me.owlaukka.currencyconversion.CurrencyConversionService;

@ApplicationScoped
public class CurrenciesResource implements CurrenciesApi {

    @Inject
    CurrencyConversionService currencyConversionService;

    @Override
    public Response getSupportedCurrencies() {
        return Response.ok(currencyConversionService.getAllSupportedCurrencies()).build();
    }
}
