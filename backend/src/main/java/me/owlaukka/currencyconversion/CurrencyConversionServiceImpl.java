package me.owlaukka.currencyconversion;

import jakarta.enterprise.context.ApplicationScoped;
import me.owlaukka.rates.ExchangeRateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@ApplicationScoped
public class CurrencyConversionServiceImpl implements CurrencyConversionService {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyConversionServiceImpl.class);
    
    private final ExchangeRateService exchangeRateService;

    CurrencyConversionServiceImpl(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
        logger.debug("CurrencyConversionServiceImpl initialized with exchange rate service");
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
        logger.debug("Converting {} {} to {}", amount, sourceCurrency, targetCurrency);
        checkCurrenciesExist(sourceCurrency, targetCurrency);

        var exchangeRate = exchangeRateService.getEuroRatesForSourceAndTargetCurrency(sourceCurrency, targetCurrency);
        logger.debug("Retrieved exchange rates for {}/{} with date: {}", sourceCurrency, targetCurrency, exchangeRate.dateOfRates());

        var sourceRate = exchangeRate.sourceRate().rate();
        var targetRate = exchangeRate.targetRate().rate();
        logger.debug("Exchange rates - {}: {}, {}: {}", sourceCurrency, sourceRate, targetCurrency, targetRate);

        var amountInEur = amount.divide(sourceRate, 10, RoundingMode.HALF_UP);
        logger.trace("Amount in EUR (intermediate): {}", amountInEur);

        var amountInTargetCurrency = amountInEur.multiply(targetRate);
        var roundedAmountInTargetCurrency = amountInTargetCurrency.setScale(2, RoundingMode.HALF_UP);

        logger.info("Converted {} {} to {} {} using rates from {}",
                amount, sourceCurrency, roundedAmountInTargetCurrency, targetCurrency, exchangeRate.dateOfRates());

        return new ConversionResult(roundedAmountInTargetCurrency, exchangeRate.dateOfRates());
    }

    @Override
    public List<String> getAllSupportedCurrencies() {
        logger.debug("Retrieving all supported currencies");
        var currencies = exchangeRateService.getAllSupportedCurrencies();
        logger.debug("Retrieved {} supported currencies", currencies.size());
        return currencies;
    }

    private void checkCurrenciesExist(String sourceCurrency, String targetCurrency) {
        logger.debug("Validating currencies: {} and {}", sourceCurrency, targetCurrency);
        
        var currencies = exchangeRateService.getCurrencies(List.of(sourceCurrency, targetCurrency));

        var isSourceCurrencyValid = currencies.stream().anyMatch(currency -> currency.equals(sourceCurrency));
        var isTargetCurrencyValid = currencies.stream().anyMatch(currency -> currency.equals(targetCurrency));

        if (!isSourceCurrencyValid && !isTargetCurrencyValid) {
            logger.warn("Invalid source and target currency provided: {} and {}", sourceCurrency, targetCurrency);
            throw new CustomValidationException("Source and target currencies are not valid", List.of("convertCurrency.sourceCurrency", "convertCurrency.targetCurrency"));
        }
        if (!isSourceCurrencyValid) {
            logger.warn("Invalid source currency provided: {}", sourceCurrency);
            throw new CustomValidationException("Source currency is not valid", List.of("convertCurrency.sourceCurrency"));
        }

        if (!isTargetCurrencyValid) {
            logger.warn("Invalid target currency provided: {}", targetCurrency);
            throw new CustomValidationException("Target currency is not valid", List.of("convertCurrency.targetCurrency"));
        }

        logger.debug("Currency validation successful for {} and {}", sourceCurrency, targetCurrency);
    }
}
