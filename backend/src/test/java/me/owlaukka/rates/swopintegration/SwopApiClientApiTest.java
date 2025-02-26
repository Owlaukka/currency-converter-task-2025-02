package me.owlaukka.rates.swopintegration;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.common.QuarkusTestResource;
import jakarta.inject.Inject;
import me.owlaukka.rates.swopintegration.model.Rate;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@QuarkusTestResource(SwopApiWireMockResource.class)
class SwopApiClientApiTest {

    @Inject
    SwopApiClientApi swopApiClientApi;

    @Test
    void testLatestRates() {
        // Given
        List<String> quoteCurrencies = List.of("EUR", "USD");
        
        // When
        List<Rate> rates = swopApiClientApi.latest(quoteCurrencies);
        
        // Then
        assertNotNull(rates);
        assertEquals(2, rates.size());
        
        // Verify EUR rate
        Rate eurRate = rates.stream()
            .filter(r -> r.getQuoteCurrency().equals("EUR"))
            .findFirst()
            .orElseThrow();
        assertEquals("BTC", eurRate.getBaseCurrency());
        assertEquals(new BigDecimal("39000.50"), eurRate.getQuote());
        assertNotNull(eurRate.getDate());
        assertFalse(eurRate.getDate().isAfter(LocalDate.now()));

        // Verify USD rate
        Rate usdRate = rates.stream()
            .filter(r -> r.getQuoteCurrency().equals("USD"))
            .findFirst()
            .orElseThrow();
        assertEquals("BTC", usdRate.getBaseCurrency());
        assertEquals(new BigDecimal("42150.75"), usdRate.getQuote());
        assertNotNull(usdRate.getDate());
        assertFalse(usdRate.getDate().isAfter(LocalDate.now()));
    }
} 