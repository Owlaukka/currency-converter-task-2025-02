package me.owlaukka.rates;

import me.owlaukka.common.Pair;

public interface ExchangeRateService {
    /**
     * Retrieves the exchange rate for a given currency pair.
     *
     * @param sourceCurrency The ISO 4217 currency code of the source currency
     * @param targetCurrency The ISO 4217 currency code of the target currency
     * @return The exchange rate from EUR to another currency
     */
    Pair<EuroExchangeRate, EuroExchangeRate> getEuroRatesForSourceAndTargetCurrency(String sourceCurrency, String targetCurrency);
}