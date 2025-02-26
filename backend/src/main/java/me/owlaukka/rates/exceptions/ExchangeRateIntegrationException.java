package me.owlaukka.rates.exceptions;

public class ExchangeRateIntegrationException extends RuntimeException {
    public ExchangeRateIntegrationException(String message) {
        super(message);
    }

    public ExchangeRateIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
