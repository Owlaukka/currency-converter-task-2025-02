package me.owlaukka.rates.swopintegration;

import io.quarkus.cache.CacheResult;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@ApplicationScoped
public class SwopExchangeRateIntegrationServiceImpl implements ExchangeRateService {
    private static final Logger logger = LoggerFactory.getLogger(SwopExchangeRateIntegrationServiceImpl.class);
    
    private final SwopApiClientApi swopApiClientApi;

    SwopExchangeRateIntegrationServiceImpl(SwopApiClientApi swopApiClientApi) {
        this.swopApiClientApi = swopApiClientApi;
        logger.debug("SwopExchangeRateIntegrationServiceImpl initialized with API client");
    }

    private static Rate findRateFromResponse(String currencyCode, List<Rate> rates) {
        logger.trace("Looking for rate for currency {} in response with {} rates", currencyCode, rates.size());
        return rates.stream()
                .filter(r -> r.quoteCurrency().equals(currencyCode))
                .findFirst()
                .orElseThrow(() -> {
                    logger.warn("Currency code '{}' not found in Swop API response", currencyCode);
                    return new ExchangeRateIntegrationBadRequestException(
                            "Given currency code '" + currencyCode + "' not found from Swop"
                    );
                });
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
    @CacheResult(cacheName = "rates")
    public EuroRatesForSourceAndTargetCurrency getEuroRatesForSourceAndTargetCurrency(
            String sourceCurrency,
            String targetCurrency
    ) throws ExchangeRateIntegrationException {
        logger.info("Fetching Euro exchange rates for source:{} and target:{}", sourceCurrency, targetCurrency);
        
        List<Rate> rates = getRatesFromSwop(sourceCurrency, targetCurrency);
        logger.debug("Retrieved {} rates from Swop API", rates.size());

        Rate sourceRate = findRateFromResponse(sourceCurrency, rates);
        Rate targetRate = findRateFromResponse(targetCurrency, rates);
        logger.debug("Found rates - source:{} ({}), target:{} ({})",
                sourceCurrency, sourceRate.quote(), targetCurrency, targetRate.quote());

        // Create response
        var sourceEuroRate = new EuroExchangeRate(sourceRate.quoteCurrency(), sourceRate.quote());
        var targetEuroRate = new EuroExchangeRate(targetRate.quoteCurrency(), targetRate.quote());

        if (!sourceRate.date().equals(targetRate.date())) {
            logger.error("Date mismatch in rates response: source date {}, target date {}",
                    sourceRate.date(), targetRate.date());
            throw new ExchangeRateIntegrationInvalidResponseException("Dates of rates from Swop are different");
        }
        var dateOfRates = sourceRate.date();

        logger.info("Successfully retrieved Euro rates for {}/{} with date: {}",
                sourceCurrency, targetCurrency, dateOfRates);
        
        return new EuroRatesForSourceAndTargetCurrency(sourceEuroRate, targetEuroRate, dateOfRates);
    }

    @Override
    @Bulkhead
    @CircuitBreaker(requestVolumeThreshold = 6)
    @Timeout(5000)
    @Retry(maxRetries = 1, delay = 1000)
    @CacheResult(cacheName = "currencies")
    // TODO: maybe make this a boolean method and return true if all currencies were found?
    public List<String> getCurrencies(List<String> currencyCodes) {
        logger.info("Validating currencies: {}", currencyCodes);

        var validCurrencies = getCurrenciesFromSwop(currencyCodes)
                .stream().map(Currency::code)
                .toList();

        logger.debug("Found {} valid currencies out of {} requested",
                validCurrencies.size(), currencyCodes.size());

        return validCurrencies;
    }

    @Override
    @Bulkhead
    @CircuitBreaker(requestVolumeThreshold = 6)
    @Timeout(5000)
    @Retry(maxRetries = 1, delay = 1000)
    @CacheResult(cacheName = "all-currencies")
    public List<String> getAllSupportedCurrencies() {
        logger.info("Retrieving all supported currencies from Swop");

        var allCurrencies = getAllCurrenciesFromSwop()
                .stream().map(Currency::code)
                .toList();

        logger.info("Retrieved {} supported currencies from Swop", allCurrencies.size());
        logger.debug("Supported currencies: {}", allCurrencies);

        return allCurrencies;
    }

    private List<Rate> getRatesFromSwop(String sourceCurrency, String targetCurrency) throws ExchangeRateIntegrationException {
        try {
            logger.debug("Making API call to Swop for rates: {} and {}", sourceCurrency, targetCurrency);
            var rates = swopApiClientApi.latest(List.of(sourceCurrency, targetCurrency));
            logger.debug("Received {} rates from Swop API", rates.size());
            return rates;
        } catch (GraphQLClientException e) {
            logger.error("Failed to get exchange rates from Swop for {} and {}: {}",
                    sourceCurrency, targetCurrency, e.getMessage(), e);
            // TODO: look at the exception and decide if it's a bad request or something else
            throw new ExchangeRateIntegrationException("Failed to get exchange rates", e);
        }
    }

    private List<Currency> getCurrenciesFromSwop(List<String> currencyCodes) {
        try {
            logger.debug("Making API call to Swop to validate currencies: {}", currencyCodes);
            var currencies = swopApiClientApi.currencies(currencyCodes);
            logger.debug("Received {} validated currencies from Swop", currencies.size());
            return currencies;
        } catch (GraphQLClientException e) {
            logger.error("Failed to get supported currencies from Swop for codes {}: {}",
                    currencyCodes, e.getMessage(), e);
            // TODO: look at the exception and decide if it's a bad request or something else
            throw new ExchangeRateIntegrationException("Failed to get supported currencies for codes: " + currencyCodes, e);
        }
    }

    private List<Currency> getAllCurrenciesFromSwop() {
        try {
            logger.debug("Making API call to Swop for all supported currencies");
            var currencies = swopApiClientApi.currencies();
            logger.debug("Received {} currencies from Swop", currencies.size());
            return currencies;
        } catch (GraphQLClientException e) {
            logger.error("Failed to get all supported currencies from Swop: {}", e.getMessage(), e);
            // TODO: look at the exception and decide if it's a bad request or something else
            throw new ExchangeRateIntegrationException("Failed to get all supported currencies from Swop", e);
        }
    }
}
