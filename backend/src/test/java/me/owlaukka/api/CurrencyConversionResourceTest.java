package me.owlaukka.api;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import me.owlaukka.currencyconversion.ConversionResult;
import me.owlaukka.currencyconversion.CurrencyConversionService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;

import static io.restassured.RestAssured.given;
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