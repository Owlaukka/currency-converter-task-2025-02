package me.owlaukka.rates;

import io.smallrye.graphql.client.GraphQLClientException;
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
    @Override
    public EuroRatesForSourceAndTargetCurrency getEuroRatesForSourceAndTargetCurrency(
            String sourceCurrency,
            String targetCurrency
    ) throws ExchangeRateIntegrationException {
        try {
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

            // TODO: what if the dates are different?
            var dateOfRates = sourceRate.date();

            return new EuroRatesForSourceAndTargetCurrency(sourceEuroRate, targetEuroRate, dateOfRates);
        } catch (GraphQLClientException e) {
            throw new ExchangeRateIntegrationException("Failed to get exchange rates", e);
        }
    }
}
