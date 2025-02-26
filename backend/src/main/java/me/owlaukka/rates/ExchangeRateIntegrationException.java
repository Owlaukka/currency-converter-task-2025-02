package me.owlaukka.rates;

public class ExchangeRateIntegrationException extends RuntimeException {
    public ExchangeRateIntegrationException(String message) {
        super(message);
    }

    public ExchangeRateIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
