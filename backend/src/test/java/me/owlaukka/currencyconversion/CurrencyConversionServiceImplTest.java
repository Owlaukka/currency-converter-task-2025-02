package me.owlaukka.currencyconversion;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import me.owlaukka.rates.EuroExchangeRate;
import me.owlaukka.rates.EuroRatesForSourceAndTargetCurrency;
import me.owlaukka.rates.ExchangeRateService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
class CurrencyConversionServiceImplTest {

    @Inject
    CurrencyConversionService currencyConversionService;

    @InjectMock
    private ExchangeRateService exchangeRateService;

    @Nested
    class ConvertCurrency {


        private static Arguments[] validCurrencyConversions() {
            return new Arguments[]{
                    Arguments.of(
                            // Source-rate
                            new BigDecimal("3.855912"),
                            // Target-rate
                            new BigDecimal("1.892903"),
                            // Given amount to convert
                            new BigDecimal("5334.53"),
                            // Expected converted amount
                            new BigDecimal("2618.77")
                    ),
                    // The expected result is actually 126.428408832233, but we're rounding it to 126.43
                    Arguments.of(
                            new BigDecimal("0.830277"),
                            new BigDecimal("1.049706"),
                            new BigDecimal("100"),
                            new BigDecimal("126.43")
                    ),
            };
        }

        @ParameterizedTest(name = "should return exchange rate {3} when source-rate is {0}, target-rate is {1} and amount to convert is {2}")
        @MethodSource("validCurrencyConversions")
        void Should_ReturnExchangeRates_When_RequestingRatesForCurrencies(
                BigDecimal returnedSourceRate,
                BigDecimal returnedTargetRate,
                BigDecimal givenAmountToConvert,
                BigDecimal expectedConvertedAmount
        ) {
            // Given
            var givenSourceCurrencyCode = "GBP";
            var givenTargetCurrencyCode = "USD";

            var returnedCurrencies = List.of(givenSourceCurrencyCode, givenTargetCurrencyCode);

            var dateOfRates = LocalDate.parse("2025-02-20");
            var returnedRates = new EuroRatesForSourceAndTargetCurrency(
                    new EuroExchangeRate(givenSourceCurrencyCode, returnedSourceRate),
                    new EuroExchangeRate(givenTargetCurrencyCode, returnedTargetRate),
                    dateOfRates
            );

            Mockito.when(exchangeRateService.getCurrencies(Mockito.anyList()))
                    .thenReturn(returnedCurrencies);
            Mockito.when(exchangeRateService.getEuroRatesForSourceAndTargetCurrency(Mockito.anyString(), Mockito.anyString()))
                    .thenReturn(returnedRates);

            // When
            var conversionResult =
                    currencyConversionService.convert(givenSourceCurrencyCode, givenTargetCurrencyCode, givenAmountToConvert);

            // Then
            var expectedConversionResult = new ConversionResult(
                    expectedConvertedAmount,
                    dateOfRates
            );
            assertEquals(expectedConversionResult, conversionResult);
        }

        @Test
        void Should_ThrowCustomValidationException_When_RequestingRatesForInvalidCurrencies() {
            // Given
            var givenSourceCurrencyCode = "YEN";
            var givenTargetCurrencyCode = "MAR";
            var givenAmountToConvert = new BigDecimal("100");

            List<String> returnedCurrencies = List.of();

            Mockito.when(exchangeRateService.getCurrencies(Mockito.anyList()))
                    .thenReturn(returnedCurrencies);

            // When + Then
            assertThrows(CustomValidationException.class, () ->
                            currencyConversionService.convert(givenSourceCurrencyCode, givenTargetCurrencyCode, givenAmountToConvert),
                    "Invalid currency codes"
            );
        }

        @Test
        void Should_ThrowCustomValidationException_When_RequestedSourceCurrencyIsNotInTheResponse() {
            // Given
            var givenSourceCurrencyCode = "GBP";
            var givenTargetCurrencyCode = "EUR";
            var givenAmountToConvert = new BigDecimal("100");

            List<String> returnedCurrencies = List.of("USD", "EUR");

            Mockito.when(exchangeRateService.getCurrencies(Mockito.anyList()))
                    .thenReturn(returnedCurrencies);

            // When + Then
            assertThrows(CustomValidationException.class, () ->
                            currencyConversionService.convert(givenSourceCurrencyCode, givenTargetCurrencyCode, givenAmountToConvert),
                    "Source currency is not valid"
            );
        }

        @Test
        void Should_ThrowCustomValidationException_When_RequestedTargetCurrencyIsNotInTheResponse() {
            // Given
            var givenSourceCurrencyCode = "USD";
            var givenTargetCurrencyCode = "GBP";
            var givenAmountToConvert = new BigDecimal("100");

            List<String> returnedCurrencies = List.of("USD", "EUR");

            Mockito.when(exchangeRateService.getCurrencies(Mockito.anyList()))
                    .thenReturn(returnedCurrencies);

            // When + Then
            var thrownException = assertThrows(CustomValidationException.class, () ->
                    currencyConversionService.convert(givenSourceCurrencyCode, givenTargetCurrencyCode, givenAmountToConvert)
            );

            assertEquals("Target currency is not valid", thrownException.getMessage());
        }

        @Test
        void Should_ThrowCustomValidationException_When_RequestedSourceAndTargetCurrencyIsNotInTheResponse() {
            // Given
            var givenSourceCurrencyCode = "USD";
            var givenTargetCurrencyCode = "GBP";
            var givenAmountToConvert = new BigDecimal("100");

            List<String> returnedCurrencies = List.of("CHF", "EUR");

            Mockito.when(exchangeRateService.getCurrencies(Mockito.anyList()))
                    .thenReturn(returnedCurrencies);

            // When + Then
            var throwException = assertThrows(CustomValidationException.class, () ->
                    currencyConversionService.convert(givenSourceCurrencyCode, givenTargetCurrencyCode, givenAmountToConvert)
            );

            assertEquals(List.of("convertCurrency.sourceCurrency", "convertCurrency.targetCurrency"), throwException.getFields());
            assertEquals("Source and target currencies are not valid", throwException.getMessage());
        }
    }

    @Nested
    class AllSupportedCurrencies {

        @Test
        void Should_ReturnAllSupportedCurrencies_When_RequestingAllSupportedCurrencies() {
            // Given
            var returnedCurrencies = List.of("USD", "EUR", "GBP");

            Mockito.when(exchangeRateService.getAllSupportedCurrencies())
                    .thenReturn(returnedCurrencies);

            // When
            var currencies = currencyConversionService.getAllSupportedCurrencies();

            // Then
            var expectedReturnedCurrencies = returnedCurrencies;
            assertEquals(expectedReturnedCurrencies, currencies);
        }

        @Test
        void Should_ThrowIllegalArgumentException_When_ExternalIntegrationFails() {
            // Given
            Mockito.when(exchangeRateService.getAllSupportedCurrencies())
                    .thenThrow(new IllegalArgumentException("errors from service"));

            // When + Then
            assertThrows(IllegalArgumentException.class,
                    () -> currencyConversionService.getAllSupportedCurrencies());
        }
    }
}
