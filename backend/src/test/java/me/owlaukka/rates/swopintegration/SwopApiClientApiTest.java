package me.owlaukka.rates.swopintegration;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import me.owlaukka.rates.swopintegration.model.Rate;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
                .filter(r -> r.quoteCurrency().equals("EUR"))
            .findFirst()
            .orElseThrow();
        assertEquals("BTC", eurRate.baseCurrency());
        assertEquals(new BigDecimal("39000.50"), eurRate.quote());
        assertNotNull(eurRate.date());
        assertFalse(eurRate.date().isAfter(LocalDate.now()));

        // Verify USD rate
        Rate usdRate = rates.stream()
                .filter(r -> r.quoteCurrency().equals("USD"))
            .findFirst()
            .orElseThrow();
        assertEquals("BTC", usdRate.baseCurrency());
        assertEquals(new BigDecimal("42150.75"), usdRate.quote());
        assertNotNull(usdRate.date());
        assertFalse(usdRate.date().isAfter(LocalDate.now()));
    }
} 