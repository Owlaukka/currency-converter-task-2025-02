package me.owlaukka.rates.exceptions;

public class ExchangeRateIntegrationBadRequestException extends RuntimeException {
    public ExchangeRateIntegrationBadRequestException(String message) {
        super(message);
    }
}
