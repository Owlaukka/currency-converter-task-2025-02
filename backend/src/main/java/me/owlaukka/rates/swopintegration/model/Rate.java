package me.owlaukka.rates.swopintegration.model;

import me.owlaukka.rates.exceptions.ExchangeRateIntegrationException;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents the response from the Swop API for a single exchange rate.
 * <p>
 * This could be generated from the GraphQL-schema.
 */
public record Rate(String baseCurrency, String quoteCurrency, BigDecimal quote, LocalDate date) {
    /**
     * Not really needed, but just in case. The Schema of Swop API does specify them being non-null.
     */
    public Rate {
        if (baseCurrency == null || quoteCurrency == null || quote == null || date == null) {
            throw new ExchangeRateIntegrationException("Rate-response from Swop API is missing required fields");
        }
        if (quote.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ExchangeRateIntegrationException("Rate-response from Swop API has a zero or non-positive rate");
        }
    }
}
