package me.owlaukka.rates.exceptions;

public class ExchangeRateIntegrationInvalidResponseException extends ExchangeRateIntegrationException {
    public ExchangeRateIntegrationInvalidResponseException(String message) {
        super(message);
    }

    public ExchangeRateIntegrationInvalidResponseException(String message, Throwable cause) {
        super(message, cause);
    }
}
