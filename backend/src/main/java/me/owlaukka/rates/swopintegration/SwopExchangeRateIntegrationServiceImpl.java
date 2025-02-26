package me.owlaukka.rates.swopintegration;

import io.smallrye.graphql.client.GraphQLClientException;
import jakarta.enterprise.context.ApplicationScoped;
import me.owlaukka.rates.EuroExchangeRate;
import me.owlaukka.rates.EuroRatesForSourceAndTargetCurrency;
import me.owlaukka.rates.ExchangeRateIntegrationException;
import me.owlaukka.rates.ExchangeRateService;
import me.owlaukka.rates.swopintegration.model.Rate;

import java.util.List;

@ApplicationScoped
public class SwopExchangeRateIntegrationServiceImpl implements ExchangeRateService {
    private final SwopApiClientApi swopApiClientApi;

    SwopExchangeRateIntegrationServiceImpl(SwopApiClientApi swopApiClientApi) {
        this.swopApiClientApi = swopApiClientApi;
    }

    private static Rate findRateFromResponse(String currencyCode, List<Rate> rates) {
        return rates.stream()
                .filter(r -> r.quoteCurrency().equals(currencyCode))
                .findFirst()
                .orElseThrow(() -> new ExchangeRateIntegrationException("Did not find requested rate for '" + currencyCode + "' in the response from integration"));
    }

    // TODO: handle error-cases more gracefully; returned list does not contain source or target, API throws
    @Override
    public EuroRatesForSourceAndTargetCurrency getEuroRatesForSourceAndTargetCurrency(
            String sourceCurrency,
            String targetCurrency
    ) throws ExchangeRateIntegrationException {
        List<Rate> rates = getRatesFromSwop(sourceCurrency, targetCurrency);

        Rate sourceRate = findRateFromResponse(sourceCurrency, rates);
        Rate targetRate = findRateFromResponse(targetCurrency, rates);

        // Create response
        var sourceEuroRate = new EuroExchangeRate(sourceCurrency, sourceRate.quote());
        var targetEuroRate = new EuroExchangeRate(targetCurrency, targetRate.quote());

        // TODO: what if the dates are different?
        var dateOfRates = sourceRate.date();

        return new EuroRatesForSourceAndTargetCurrency(sourceEuroRate, targetEuroRate, dateOfRates);
    }

    private List<Rate> getRatesFromSwop(String sourceCurrency, String targetCurrency) throws ExchangeRateIntegrationException {
        try {
            return swopApiClientApi.latest(List.of(sourceCurrency, targetCurrency));
        } catch (GraphQLClientException e) {
            throw new ExchangeRateIntegrationException("Failed to get exchange rates", e);
        }
    }
}
