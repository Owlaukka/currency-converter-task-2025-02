package me.owlaukka.currencyconversion;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents the result of a currency conversion operation.
 */
public record ConversionResult(BigDecimal convertedAmount, LocalDate date) {
}