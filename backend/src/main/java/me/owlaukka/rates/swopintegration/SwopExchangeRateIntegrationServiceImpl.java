package me.owlaukka.rates.swopintegration;

import io.smallrye.graphql.client.GraphQLClientException;
import jakarta.enterprise.context.ApplicationScoped;
import me.owlaukka.rates.EuroExchangeRate;
import me.owlaukka.rates.EuroRatesForSourceAndTargetCurrency;
import me.owlaukka.rates.ExchangeRateService;
import me.owlaukka.rates.exceptions.ExchangeRateIntegrationBadRequestException;
import me.owlaukka.rates.exceptions.ExchangeRateIntegrationException;
import me.owlaukka.rates.exceptions.ExchangeRateIntegrationInvalidResponseException;
import me.owlaukka.rates.swopintegration.model.Currency;
import me.owlaukka.rates.swopintegration.model.Rate;
import org.eclipse.microprofile.faulttolerance.Bulkhead;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;

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
                .orElseThrow(() ->
                        new ExchangeRateIntegrationBadRequestException(
                                "Given currency code '" + currencyCode + "' not found from Swop"
                        ));
    }

    /**
     * Retrieves the latest exchange rates from EUR to a given list of quote currencies.
     * <p>
     * <li>Supports maximum 10 concurrent calls to the external API (bulkhead).</li>
     * <li>Will stop allowing requests for 5 seconds when half of 6 requests fail (circuit breaker).</li>
     * <li>Will timeout after 5 seconds.</li>
     * <li>Will retry once with a 1 second delay if the request fails. This should be tweaked to only
     * retry on certain exceptions.</li>
     *
     * @param sourceCurrency The ISO 4217 currency code of the source currency
     * @param targetCurrency The ISO 4217 currency code of the target currency
     * @return The exchange rate from EUR to given currencies
     * @throws ExchangeRateIntegrationException If the request fails to Swop
     */
    @Override
    @Bulkhead
    @CircuitBreaker(requestVolumeThreshold = 6, skipOn = ExchangeRateIntegrationInvalidResponseException.class)
    @Timeout(5000)
    @Retry(maxRetries = 1, delay = 1000, abortOn = ExchangeRateIntegrationInvalidResponseException.class)
    public EuroRatesForSourceAndTargetCurrency getEuroRatesForSourceAndTargetCurrency(
            String sourceCurrency,
            String targetCurrency
    ) throws ExchangeRateIntegrationException {
        List<Rate> rates = getRatesFromSwop(sourceCurrency, targetCurrency);

        Rate sourceRate = findRateFromResponse(sourceCurrency, rates);
        Rate targetRate = findRateFromResponse(targetCurrency, rates);

        // Create response
        var sourceEuroRate = new EuroExchangeRate(sourceRate.quoteCurrency(), sourceRate.quote());
        var targetEuroRate = new EuroExchangeRate(targetRate.quoteCurrency(), targetRate.quote());

        if (!sourceRate.date().equals(targetRate.date())) {
            throw new ExchangeRateIntegrationInvalidResponseException("Dates of rates from Swop are different");
        }
        var dateOfRates = sourceRate.date();

        return new EuroRatesForSourceAndTargetCurrency(sourceEuroRate, targetEuroRate, dateOfRates);
    }

    @Override
    @Bulkhead
    @CircuitBreaker(requestVolumeThreshold = 6)
    @Timeout(5000)
    @Retry(maxRetries = 1, delay = 1000)
    public List<String> getCurrencies(List<String> currencyCodes) {
        return getCurrenciesFromSwop(currencyCodes)
                .stream().map(Currency::code)
                .toList();
    }

    @Override
    @Bulkhead
    @CircuitBreaker(requestVolumeThreshold = 6)
    @Timeout(5000)
    @Retry(maxRetries = 1, delay = 1000)
    public List<String> getAllSupportedCurrencies() {
        return getAllCurrenciesFromSwop()
                .stream().map(Currency::code)
                .toList();
    }

    private List<Rate> getRatesFromSwop(String sourceCurrency, String targetCurrency) throws ExchangeRateIntegrationException {
        try {
            return swopApiClientApi.latest(List.of(sourceCurrency, targetCurrency));
        } catch (GraphQLClientException e) {
            // TODO: look at the exception and decide if it's a bad request or something else
            throw new ExchangeRateIntegrationException("Failed to get exchange rates", e);
        }
    }

    private List<Currency> getCurrenciesFromSwop(List<String> currencyCodes) {
        try {
            return swopApiClientApi.currencies(currencyCodes);
        } catch (GraphQLClientException e) {
            // TODO: look at the exception and decide if it's a bad request or something else
            throw new ExchangeRateIntegrationException("Failed to get supported currencies for codes: " + currencyCodes, e);
        }
    }

    private List<Currency> getAllCurrenciesFromSwop() {
        try {
            return swopApiClientApi.currencies();
        } catch (GraphQLClientException e) {
            // TODO: look at the exception and decide if it's a bad request or something else
            throw new ExchangeRateIntegrationException("Failed to get all supported currencies from Swop", e);
        }
    }
}
