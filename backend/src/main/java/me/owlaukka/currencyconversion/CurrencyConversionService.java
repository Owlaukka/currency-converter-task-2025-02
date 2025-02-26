package me.owlaukka.currencyconversion;

import java.math.BigDecimal;

/**
 * Service interface for currency conversion operations.
 */
public interface CurrencyConversionService {
    /**
     * Converts an amount from one currency to another.
     *
     * @param sourceCurrency The ISO 4217 currency code of the source currency
     * @param targetCurrency The ISO 4217 currency code of the target currency
     * @param amount         The amount to convert (must be positive)
     * @return A ConversionResult containing the converted amount and the date of conversion
     * @throws IllegalArgumentException    if the currency codes are invalid or amount is not positive
     * @throws CurrencyConversionException if the conversion fails due to external service issues
     */
    ConversionResult convert(String sourceCurrency, String targetCurrency, BigDecimal amount);
} 