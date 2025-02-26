package me.owlaukka.api;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.Response;
import me.owlaukka.currencyconversion.CurrencyConversionService;
import me.owlaukka.rates.exceptions.ExchangeRateIntegrationException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

@QuarkusTest
public class CurrenciesResourceTest {

    @InjectMock
    CurrencyConversionService currencyConversionService;

    @Test
    void Should_ReturnConversion() {
        var returnedCurrencies = List.of("USD", "EUR", "JPY", "GBP");

        Mockito.when(currencyConversionService.getAllSupportedCurrencies())
                .thenReturn(returnedCurrencies);

        given()
                .when()
                .get("/currencies")
                .then()
                .statusCode(200)
                .body("$", equalTo(returnedCurrencies));
    }

    @Test
    void Should_Return500Error_When_ConversionThrowsAGenericException() {
        Mockito.when(currencyConversionService.getAllSupportedCurrencies())
                .thenThrow(new RuntimeException("Something went wrong"));

        given()
                .when()
                .get("/currencies")
                .then()
                .statusCode(500)
                .body("code", equalTo("INTERNAL_SERVER_ERROR"))
                .body("message", equalTo("Something went wrong"));
    }

    @Test
    void Should_Return503Error_When_ExternalIntegrationFails() {
        Mockito.when(currencyConversionService.getAllSupportedCurrencies())
                .thenThrow(new ExchangeRateIntegrationException("Swop failed"));
        given()
                .when()
                .get("/currencies")
                .then()
                .statusCode(503)
                .body("code", equalTo(Response.Status.SERVICE_UNAVAILABLE.name()))
                .body("message", equalTo("Service temporarily unavailable"));
    }
}
