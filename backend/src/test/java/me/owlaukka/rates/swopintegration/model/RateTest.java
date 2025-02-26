package me.owlaukka.rates.swopintegration.model;

import me.owlaukka.rates.ExchangeRateIntegrationException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class RateTest {

    @ParameterizedTest(name = "should throw exception when rate is {0}")
    @ValueSource(doubles = {0, -0.1})
    void Should_ThrowIntegrationException_When_SwopResponseRateContainsAQuoteLessZeroOrLess(double quote) {
        // When + Then
        assertThrows(ExchangeRateIntegrationException.class,
                () -> new Rate("EUR", "USD", new BigDecimal(quote), LocalDate.now()));
    }
}
