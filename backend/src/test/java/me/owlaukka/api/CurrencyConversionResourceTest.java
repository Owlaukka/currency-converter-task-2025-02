package me.owlaukka.api;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.Response;
import me.owlaukka.currencyconversion.ConversionResult;
import me.owlaukka.currencyconversion.CurrencyConversionService;
import me.owlaukka.rates.ExchangeRateIntegrationException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsStringIgnoringCase;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

@QuarkusTest
class CurrencyConversionResourceTest {

    @InjectMock
    CurrencyConversionService currencyConversionService;

    @Test
    void Should_ReturnConversion_When_GivenValidBasicData() {
        var conversionResult = new ConversionResult(new BigDecimal("85.00"), LocalDate.now());

        Mockito.when(currencyConversionService.convert("USD", "EUR", new BigDecimal("100.50")))
                .thenReturn(conversionResult);

        given()
            .when()
            .queryParam("sourceCurrency", "USD")
            .queryParam("targetCurrency", "EUR")
            .queryParam("amount", "100.50")
            .get("/conversion")
            .then()
            .statusCode(200)
                .body("convertedAmount", equalTo(conversionResult.convertedAmount().floatValue()))
                .body("date", equalTo(conversionResult.date().toString()));
    }

    @Test
    void Should_Return500Error_When_ConversionThrowsAGenericException() {
        Mockito.when(currencyConversionService.convert("USD", "EUR", new BigDecimal("100.50")))
                .thenThrow(new RuntimeException("Something went wrong"));

        given()
                .when()
                .queryParam("sourceCurrency", "USD")
                .queryParam("targetCurrency", "EUR")
                .queryParam("amount", "100.50")
                .get("/conversion")
                .then()
                .statusCode(500)
                .body("code", equalTo("INTERNAL_SERVER_ERROR"))
                .body("message", equalTo("Something went wrong"));
    }

    @Test
    void Should_Return503Error_When_ExternalIntegrationFails() {
        Mockito.when(currencyConversionService.convert(Mockito.anyString(), Mockito.anyString(), Mockito.any(BigDecimal.class)))
                .thenThrow(new ExchangeRateIntegrationException("Swop failed"));
        given()
                .when()
                .queryParam("sourceCurrency", "USD")
                .queryParam("targetCurrency", "GBP")
                .queryParam("amount", "123")
                .get("/conversion")
                .then()
                .statusCode(503)
                .body("code", equalTo(Response.Status.SERVICE_UNAVAILABLE.name()))
                .body("message", equalTo("Service temporarily unavailable"));
    }

    @Test
    void Should_Return400Error_When_NoSourceCurrencyIsNotGiven() {
        given()
                .when()
                .queryParam("targetCurrency", "EUR")
                .queryParam("amount", "100.50")
                .get("/conversion")
                .then()
                .statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("message", equalTo("Invalid input parameters: convertCurrency.sourceCurrency: must not be null"));
    }

    @Test
    void Should_Return400Error_When_NoTargetCurrencyIsNotGiven() {
        given()
                .when()
                .queryParam("sourceCurrency", "EUR")
                .queryParam("amount", "100.50")
                .get("/conversion")
                .then()
                .statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("message", equalTo("Invalid input parameters: convertCurrency.targetCurrency: must not be null"));
    }

    @Test
    void Should_Return400Error_When_NoAmountIsNotGiven() {
        given()
                .when()
                .queryParam("sourceCurrency", "USD")
                .queryParam("targetCurrency", "GBP")
                .get("/conversion")
                .then()
                .statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("message", equalTo("Invalid input parameters: convertCurrency.amount: must not be null"));
    }

    @Test
    void Should_Return400Error_When_CurrencyCodeIsInvalid() {
        given()
                .when()
                .queryParam("sourceCurrency", "USDA")
                .queryParam("targetCurrency", "G")
                .get("/conversion")
                .then()
                .statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("message", containsStringIgnoringCase("sourceCurrency: size must be between 3 and 3"))
                .body("message", containsStringIgnoringCase("targetCurrency: size must be between 3 and 3"));
    }

    @Test
    @Disabled("TODO: not yet implemented")
    void testInvalidAmount_Negative() {
        given()
            .when()
            .queryParam("sourceCurrency", "USD")
            .queryParam("targetCurrency", "EUR")
            .queryParam("amount", "-100")
            .get("/conversion")
            .then()
            .statusCode(400)
            .body("code", is("VALIDATION_ERROR"))
            .body("message", notNullValue());
    }

    @Test
    @Disabled("TODO: not yet implemented")
    void testInvalidAmount_Zero() {
        given()
            .when()
            .queryParam("sourceCurrency", "USD")
            .queryParam("targetCurrency", "EUR")
            .queryParam("amount", "0")
            .get("/conversion")
            .then()
            .statusCode(400)
            .body("code", is("VALIDATION_ERROR"))
            .body("message", notNullValue());
    }
    
    @Test
    @Disabled("TODO: not yet implemented")
    void testInvalidCurrencyCode() {
        given()
            .when()
            .queryParam("sourceCurrency", "INVALID")
            .queryParam("targetCurrency", "EUR")
            .queryParam("amount", "100")
            .get("/conversion")
            .then()
            .statusCode(400)
            .body("code", is("VALIDATION_ERROR"))
            .body("message", notNullValue());
    }
} 