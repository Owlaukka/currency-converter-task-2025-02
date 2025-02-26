package me.owlaukka.rates;

import jakarta.enterprise.context.ApplicationScoped;
import me.owlaukka.rates.swopintegration.SwopApiClientApi;

import java.util.List;

@ApplicationScoped
public class ExchangeRateServiceImpl implements ExchangeRateService {
    private final SwopApiClientApi swopApiClientApi;

    ExchangeRateServiceImpl(SwopApiClientApi swopApiClientApi) {
        this.swopApiClientApi = swopApiClientApi;
    }

    // TODO: handle error-cases more gracefully; returned list does not contain source or target, API throws
    // TODO: replace Pair with a more descriptive class
    @Override
    public EuroRatesForSourceAndTargetCurrency getEuroRatesForSourceAndTargetCurrency(String sourceCurrency, String targetCurrency) {
        var rates = swopApiClientApi.latest(List.of(sourceCurrency, targetCurrency));
        var sourceRate = rates.stream()
                .filter(r -> r.quoteCurrency().equals(sourceCurrency))
                .findFirst()
                .orElseThrow();

        var targetRate = rates.stream()
                .filter(r -> r.quoteCurrency().equals(targetCurrency))
                .findFirst()
                .orElseThrow();

        var sourceEuroRate = new EuroExchangeRate(sourceCurrency, sourceRate.quote());
        var targetEuroRate = new EuroExchangeRate(targetCurrency, targetRate.quote());

        return new EuroRatesForSourceAndTargetCurrency(sourceEuroRate, targetEuroRate);
    }
}
