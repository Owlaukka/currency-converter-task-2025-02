package me.owlaukka.currencyconversion;

/**
 * Exception thrown when currency conversion operations fail.
 */
public class CurrencyConversionException extends RuntimeException {
    public CurrencyConversionException(String message) {
        super(message);
    }

    public CurrencyConversionException(String message, Throwable cause) {
        super(message, cause);
    }
} 