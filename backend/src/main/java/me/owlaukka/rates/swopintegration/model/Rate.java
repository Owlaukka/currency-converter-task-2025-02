package me.owlaukka.rates.swopintegration.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents the response from the Swop API for a single exchange rate.
 * <p>
 * This could be generated from the GraphQL-schema.
 */
public record Rate(String baseCurrency, String quoteCurrency, BigDecimal quote, LocalDate date) {
}
