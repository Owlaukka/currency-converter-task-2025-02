package me.owlaukka.currencyconversion;

import jakarta.enterprise.context.ApplicationScoped;
import me.owlaukka.rates.ExchangeRateService;

import java.math.BigDecimal;
import java.math.RoundingMode;

@ApplicationScoped
public class CurrencyConversionServiceImpl implements CurrencyConversionService {

    private final ExchangeRateService exchangeRateService;

    CurrencyConversionServiceImpl(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    /**
     * Converts an amount from one currency to another.
     * <p>
     * TODO: add error-case handling
     *
     * @param sourceCurrency The ISO 4217 currency code of the source currency
     * @param targetCurrency The ISO 4217 currency code of the target currency
     * @param amount         The amount to convert (must be positive)
     * @return A ConversionResult containing the converted amount and the date of conversion
     * @throws IllegalArgumentException    if the currency codes are invalid or amount is not positive
     * @throws CurrencyConversionException if the conversion fails due to external service issues
     */
    @Override
    public ConversionResult convert(String sourceCurrency, String targetCurrency, BigDecimal amount) {
        var exchangeRate = exchangeRateService.getEuroRatesForSourceAndTargetCurrency(sourceCurrency, targetCurrency);
        var sourceRate = exchangeRate.sourceRate().rate();
        var targetRate = exchangeRate.targetRate().rate();

        var amountInEur = amount.divide(sourceRate, 10, RoundingMode.HALF_UP);

        var amountInTargetCurrency = amountInEur.multiply(targetRate);

        var roundedAmountInTargetCurrency = amountInTargetCurrency.setScale(2, RoundingMode.HALF_UP);

        return new ConversionResult(roundedAmountInTargetCurrency, exchangeRate.dateOfRates());
    }
}
