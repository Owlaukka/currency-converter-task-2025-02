package me.owlaukka.rates;

import java.util.List;

public interface ExchangeRateService {
    /**
     * Retrieves the exchange rate for a given currency pair.
     *
     * @param sourceCurrency The ISO 4217 currency code of the source currency
     * @param targetCurrency The ISO 4217 currency code of the target currency
     * @return The exchange rate from EUR to another currency
     */
    EuroRatesForSourceAndTargetCurrency getEuroRatesForSourceAndTargetCurrency(
            String sourceCurrency,
            String targetCurrency
    );

    List<String> getCurrencies(List<String> currencyCodes);

    List<String> getAllSupportedCurrencies();
}