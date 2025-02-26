package me.owlaukka.rates.swopintegration;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.graphql.client.GraphQLClientException;
import jakarta.inject.Inject;
import me.owlaukka.rates.EuroExchangeRate;
import me.owlaukka.rates.EuroRatesForSourceAndTargetCurrency;
import me.owlaukka.rates.ExchangeRateIntegrationException;
import me.owlaukka.rates.swopintegration.model.Rate;
import org.junit.jupiter.api.BeforeEach;
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


    @BeforeEach
    void setUp() {
        Mockito.when(swopApiClientApi.latest(Mockito.anyList())).thenReturn(List.of());
    }

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
        assertThrows(ExchangeRateIntegrationException.class,
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
