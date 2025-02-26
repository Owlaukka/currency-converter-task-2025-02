package me.owlaukka.rates;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import me.owlaukka.common.Pair;
import me.owlaukka.rates.swopintegration.SwopApiClientApi;
import me.owlaukka.rates.swopintegration.model.Rate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class ExchangeRateServiceImplTest {

    @Inject
    ExchangeRateServiceImpl exchangeRateService;

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
                new Rate("EUR", "USD", new BigDecimal("1.0423"), LocalDate.now()),
                new Rate("EUR", "CHF", new BigDecimal("54.58345"), LocalDate.now())
        );
        String sourceCurrency = "USD";
        String targetCurrency = "CHF";

        Mockito.when(swopApiClientApi.latest(List.of(sourceCurrency, targetCurrency)))
                .thenReturn(returnedRates);

        // When
        var rates = exchangeRateService.getEuroRatesForSourceAndTargetCurrency(sourceCurrency, targetCurrency);

        // Then
        var expectedRates = new Pair<>(
                new EuroExchangeRate("USD", new BigDecimal("1.0423")),
                new EuroExchangeRate("CHF", new BigDecimal("54.58345"))
        );
        assertEquals(expectedRates, rates);
    }

    @Test
    void Should_ReturnExchangeRates_When_ReceivedCurrenciesAreInADifferentOrder() {
        // Given
        var returnedRates = List.of(
                new Rate("EUR", "GBP", new BigDecimal("5"), LocalDate.now()),
                new Rate("EUR", "SGD", new BigDecimal("1.000012"), LocalDate.now())
        );
        String sourceCurrency = "SGD";
        String targetCurrency = "GBP";

        Mockito.when(swopApiClientApi.latest(List.of(sourceCurrency, targetCurrency)))
                .thenReturn(returnedRates);

        // When
        var rates = exchangeRateService.getEuroRatesForSourceAndTargetCurrency(sourceCurrency, targetCurrency);

        // Then
        var expectedRates = new Pair<>(
                new EuroExchangeRate("SGD", new BigDecimal("1.000012")),
                new EuroExchangeRate("GBP", new BigDecimal("5"))
        );
        assertEquals(expectedRates, rates);
    }
}
