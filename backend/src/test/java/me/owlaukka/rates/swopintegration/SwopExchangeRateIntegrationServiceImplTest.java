package me.owlaukka.rates.swopintegration;

import io.quarkus.cache.CacheManager;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.graphql.client.GraphQLClientException;
import jakarta.inject.Inject;
import me.owlaukka.rates.EuroExchangeRate;
import me.owlaukka.rates.EuroRatesForSourceAndTargetCurrency;
import me.owlaukka.rates.exceptions.ExchangeRateIntegrationBadRequestException;
import me.owlaukka.rates.exceptions.ExchangeRateIntegrationException;
import me.owlaukka.rates.swopintegration.model.Currency;
import me.owlaukka.rates.swopintegration.model.Rate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
class SwopExchangeRateIntegrationServiceImplTest {

    @Inject
    SwopExchangeRateIntegrationServiceImpl exchangeRateService;

    @InjectMock
    private SwopApiClientApi swopApiClientApi;

    @Inject
    CacheManager cacheManager;

    @BeforeEach
    void clearCache() {
        cacheManager.getCache("all-currencies").get().invalidateAll().await().indefinitely();
        cacheManager.getCache("currencies").get().invalidateAll().await().indefinitely();
    }

    @Nested
    class FindRateFromResponseTests {

        @Test
        void Should_ReturnExchangeRates_When_RequestingRatesForNonEURCurrencies() {
            // Given
            var returnedRates = List.of(
                    new Rate("EUR", "USD", new BigDecimal("1.0423"), LocalDate.parse("2025-01-30")),
                    new Rate("EUR", "CHF", new BigDecimal("54.58345"), LocalDate.parse("2025-01-30"))
            );
            String sourceCurrency = "USD";
            String targetCurrency = "CHF";

            Mockito.when(swopApiClientApi.latest(List.of(sourceCurrency, targetCurrency)))
                    .thenReturn(returnedRates);

            // When
            var rates = exchangeRateService.getEuroRatesForSourceAndTargetCurrency(sourceCurrency, targetCurrency);

            // Then
            var expectedRates = new EuroRatesForSourceAndTargetCurrency(
                    new EuroExchangeRate("USD", new BigDecimal("1.0423")),
                    new EuroExchangeRate("CHF", new BigDecimal("54.58345")),
                    LocalDate.parse("2025-01-30")
            );
            assertEquals(expectedRates, rates);
        }

        @Test
        void Should_ReturnExchangeRates_When_ReceivedCurrenciesAreInADifferentOrder() {
            // Given
            var returnedRates = List.of(
                    new Rate("EUR", "GBP", new BigDecimal("5"), LocalDate.parse("2025-02-04")),
                    new Rate("EUR", "SGD", new BigDecimal("1.000012"), LocalDate.parse("2025-02-04"))
            );
            String sourceCurrency = "SGD";
            String targetCurrency = "GBP";

            Mockito.when(swopApiClientApi.latest(List.of(sourceCurrency, targetCurrency)))
                    .thenReturn(returnedRates);

            // When
            var rates = exchangeRateService.getEuroRatesForSourceAndTargetCurrency(sourceCurrency, targetCurrency);

            // Then
            var expectedRates = new EuroRatesForSourceAndTargetCurrency(
                    new EuroExchangeRate("SGD", new BigDecimal("1.000012")),
                    new EuroExchangeRate("GBP", new BigDecimal("5")),
                    LocalDate.parse("2025-02-04")
            );
            assertEquals(expectedRates, rates);
        }

        @Test
        void Should_ThrowIntegrationException_When_ExternalIntegrationFails() {
            // Given
            String sourceCurrency = "SGD";
            String targetCurrency = "GBP";

            Mockito.when(swopApiClientApi.latest(List.of(sourceCurrency, targetCurrency)))
                    .thenThrow(new GraphQLClientException("errors from service", List.of()));

            // When + Then
            assertThrows(ExchangeRateIntegrationException.class,
                    () -> exchangeRateService.getEuroRatesForSourceAndTargetCurrency(sourceCurrency, targetCurrency));
        }

        @Test
        void Should_ThrowIntegrationException_When_SwopResponseDoesNotContainRequestedInformation() {
            // Given
            var returnedRates = List.of(
                    new Rate("EUR", "AGF", new BigDecimal("1.0423"), LocalDate.parse("2025-01-30")),
                    new Rate("EUR", "WER", new BigDecimal("54.58345"), LocalDate.parse("2025-02-04"))
            );
            String sourceCurrency = "USD";
            String targetCurrency = "CHF";

            Mockito.when(swopApiClientApi.latest(List.of(sourceCurrency, targetCurrency)))
                    .thenReturn(returnedRates);

            // When + Then
            assertThrows(ExchangeRateIntegrationBadRequestException.class,
                    () -> exchangeRateService.getEuroRatesForSourceAndTargetCurrency(sourceCurrency, targetCurrency));
        }

        @Test
        void Should_ThrowIntegrationException_When_SwopResponseContainsInconsistentDates() {
            // Given
            // Not the same. This should probably not actually ever happen
            var usdRateDate = LocalDate.parse("2025-01-30");
            var chfRateDate = LocalDate.parse("2025-02-04");

            String sourceCurrency = "USD";
            String targetCurrency = "CHF";

            var returnedRates = List.of(
                    new Rate("EUR", sourceCurrency, new BigDecimal("1.0423"), usdRateDate),
                    new Rate("EUR", targetCurrency, new BigDecimal("54.58345"), chfRateDate)
            );

            Mockito.when(swopApiClientApi.latest(List.of(sourceCurrency, targetCurrency)))
                    .thenReturn(returnedRates);

            // When + Then
            assertThrows(ExchangeRateIntegrationException.class,
                    () -> exchangeRateService.getEuroRatesForSourceAndTargetCurrency(sourceCurrency, targetCurrency));
        }
    }

    @Nested
    class GetCurrenciesTests {

        @Test
        void Should_ReturnCurrencies_When_RequestingMultipleCurrencies() {
            // Given
            var currencyCodes = List.of("USD", "CHF", "GBP");
            var returnedCurrencies = List.of(
                    new Currency("USD"),
                    new Currency("CHF"),
                    new Currency("GBP")
            );

            Mockito.when(swopApiClientApi.currencies(currencyCodes))
                    .thenReturn(returnedCurrencies);

            // When
            var currencies = exchangeRateService.getCurrencies(currencyCodes);

            // Then
            var expectedReturnedCurrencies = List.of("USD", "CHF", "GBP");
            assertEquals(expectedReturnedCurrencies, currencies);
        }

        @Test
        void Should_ReturnCurrenciesFromCache_When_RequestingSameCurrenciesMultipleTimes() {
            // Given
            var currencyCodes = List.of("USD", "CHF", "GBP");
            var returnedCurrencies = List.of(
                    new Currency("USD"),
                    new Currency("CHF"),
                    new Currency("GBP")
            );

            Mockito.when(swopApiClientApi.currencies(currencyCodes))
                    .thenReturn(returnedCurrencies);

            // When
            // Called twice
            exchangeRateService.getCurrencies(currencyCodes);
            var currencies = exchangeRateService.getCurrencies(currencyCodes);

            // Then
            var expectedReturnedCurrencies = List.of("USD", "CHF", "GBP");
            assertEquals(expectedReturnedCurrencies, currencies);

            Mockito.verify(swopApiClientApi, Mockito.times(1)).currencies(currencyCodes);
        }

        @Test
        void Should_ThrowIntegrationException_When_ExternalIntegrationFails() {
            // Given
            var currencyCodes = List.of("USD", "CHF", "GBP");

            Mockito.when(swopApiClientApi.currencies(currencyCodes))
                    .thenThrow(new GraphQLClientException("errors from service", List.of()));

            // When + Then
            assertThrows(ExchangeRateIntegrationException.class,
                    () -> exchangeRateService.getCurrencies(currencyCodes));
        }
    }

    @Nested
    class GetAllSupportedCurrenciesTests {

        @Test
        void Should_ReturnCurrencies_When_RequestingMultipleCurrencies() {
            // Given
            var returnedCurrencies = List.of(
                    new Currency("USD"),
                    new Currency("EUR"),
                    new Currency("GBP")
            );

            Mockito.when(swopApiClientApi.currencies())
                    .thenReturn(returnedCurrencies);

            // When
            var currencies = exchangeRateService.getAllSupportedCurrencies();

            // Then
            var expectedReturnedCurrencies = List.of("USD", "EUR", "GBP");
            assertEquals(expectedReturnedCurrencies, currencies);
        }

        @Test
        void Should_ReturnCurrenciesFromCache_When_RequestingSameCurrenciesMultipleTimes() {
            // Given
            var returnedCurrencies = List.of(
                    new Currency("USD"),
                    new Currency("CHF"),
                    new Currency("GBP")
            );

            Mockito.when(swopApiClientApi.currencies())
                    .thenReturn(returnedCurrencies);

            // When
            // Called twice
            exchangeRateService.getAllSupportedCurrencies();
            var currencies = exchangeRateService.getAllSupportedCurrencies();

            // Then
            var expectedReturnedCurrencies = List.of("USD", "CHF", "GBP");
            assertEquals(expectedReturnedCurrencies, currencies);

            Mockito.verify(swopApiClientApi, Mockito.times(1)).currencies();
        }

        @Test
        void Should_ThrowIntegrationException_When_ExternalIntegrationFails() {
            // Given
            Mockito.when(swopApiClientApi.currencies())
                    .thenThrow(new GraphQLClientException("errors from service", List.of()));

            // When + Then
            assertThrows(ExchangeRateIntegrationException.class,
                    () -> exchangeRateService.getAllSupportedCurrencies());
        }
    }
}
