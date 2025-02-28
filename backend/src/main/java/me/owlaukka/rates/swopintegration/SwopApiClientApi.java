package me.owlaukka.rates.swopintegration;

import io.smallrye.graphql.client.typesafe.api.GraphQLClientApi;
import me.owlaukka.rates.swopintegration.model.Currency;
import me.owlaukka.rates.swopintegration.model.Rate;
import org.eclipse.microprofile.faulttolerance.Bulkhead;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.graphql.NonNull;

import java.util.List;

@GraphQLClientApi(configKey = "swop-api")
public interface SwopApiClientApi {

    /**
     * Retrieves the latest exchange rates from EUR to a given list of quote currencies.
     * <p>
     * <li>Supports maximum 10 concurrent calls to the external API (bulkhead).</li>
     * <li>Will stop allowing requests for 5 seconds when half of 6 requests fail (circuit breaker).</li>
     * <li>Will timeout after 5 seconds.</li>
     * <li>Will retry once with a 1 second delay if the request fails. This should be tweaked to only
     * retry on certain exceptions.</li>
     *
     * @param quoteCurrencies List of ISO 4217 currency codes to get rates for
     * @return List of Rates from EUR to the given quote currencies
     */
    @Bulkhead
    @CircuitBreaker(requestVolumeThreshold = 6)
    @Timeout(5000)
    @Retry(maxDuration = 3000, delay = 1000)
    List<Rate> latest(List<@NonNull String> quoteCurrencies);

    @Bulkhead
    @CircuitBreaker(requestVolumeThreshold = 6)
    @Timeout(5000)
    @Retry(maxDuration = 3000, delay = 1000)
    List<Currency> currencies(List<@NonNull String> currencyCodes);

    @Bulkhead
    @CircuitBreaker(requestVolumeThreshold = 6)
    @Timeout(5000)
    @Retry(maxDuration = 3000, delay = 1000)
    List<Currency> currencies();
}
