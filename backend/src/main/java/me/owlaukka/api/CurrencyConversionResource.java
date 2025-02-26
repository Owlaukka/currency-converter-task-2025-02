package me.owlaukka.api;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import me.owlaukka.currencyconversion.CurrencyConversionService;
import me.owlaukka.model.ConversionResponse;

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
}
