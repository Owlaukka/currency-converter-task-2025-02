package me.owlaukka.currencyconversion;

import jakarta.enterprise.context.ApplicationScoped;
import me.owlaukka.rates.ExchangeRateService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@ApplicationScoped
public class CurrencyConversionServiceImpl implements CurrencyConversionService {

    private final ExchangeRateService exchangeRateService;

    CurrencyConversionServiceImpl(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    /**
     * Converts an amount from one currency to another.
     *
     * @param sourceCurrency The ISO 4217 currency code of the source currency
     * @param targetCurrency The ISO 4217 currency code of the target currency
     * @param amount         The amount to convert
     * @return A ConversionResult containing the converted amount and the date of conversion
     */
    @Override
    public ConversionResult convert(String sourceCurrency, String targetCurrency, BigDecimal amount) {
        // TODO: parallelize these requests?
        checkCurrenciesExist(sourceCurrency, targetCurrency);

        var exchangeRate = exchangeRateService.getEuroRatesForSourceAndTargetCurrency(sourceCurrency, targetCurrency);

        var sourceRate = exchangeRate.sourceRate().rate();
        var targetRate = exchangeRate.targetRate().rate();

        var amountInEur = amount.divide(sourceRate, 10, RoundingMode.HALF_UP);

        var amountInTargetCurrency = amountInEur.multiply(targetRate);

        var roundedAmountInTargetCurrency = amountInTargetCurrency.setScale(2, RoundingMode.HALF_UP);

        return new ConversionResult(roundedAmountInTargetCurrency, exchangeRate.dateOfRates());
    }

    @Override
    public List<String> getAllSupportedCurrencies() {
        return exchangeRateService.getAllSupportedCurrencies();
    }

    private void checkCurrenciesExist(String sourceCurrency, String targetCurrency) {
        var currencies = exchangeRateService.getCurrencies(List.of(sourceCurrency, targetCurrency));

        var isSourceCurrencyValid = currencies.stream().anyMatch(currency -> currency.equals(sourceCurrency));
        var isTargetCurrencyValid = currencies.stream().anyMatch(currency -> currency.equals(targetCurrency));

        if (!isSourceCurrencyValid) {
            throw new IllegalArgumentException("Source currency is not valid");
        }

        if (!isTargetCurrencyValid) {
            throw new IllegalArgumentException("Target currency is not valid");
        }
    }
}
