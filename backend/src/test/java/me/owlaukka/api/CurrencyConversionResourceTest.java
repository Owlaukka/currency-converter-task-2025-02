package me.owlaukka.api;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.Response;
import me.owlaukka.currencyconversion.ConversionResult;
import me.owlaukka.currencyconversion.CurrencyConversionService;
import me.owlaukka.rates.exceptions.ExchangeRateIntegrationBadRequestException;
import me.owlaukka.rates.exceptions.ExchangeRateIntegrationException;
import me.owlaukka.rates.exceptions.ExchangeRateIntegrationInvalidResponseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsStringIgnoringCase;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class CurrencyConversionResourceTest {

    @InjectMock
    CurrencyConversionService currencyConversionService;

    @ParameterizedTest(name = "should return conversion when given valid data with amount {0}")
    @ValueSource(strings = {"5", "1000.45", "543.4"})
    void Should_ReturnConversion_When_GivenValidBasicData(String amount) {
        var conversionResult = new ConversionResult(new BigDecimal("85.00"), LocalDate.now());

        Mockito.when(currencyConversionService.convert("USD", "EUR", new BigDecimal(amount)))
                .thenReturn(conversionResult);

        given()
            .when()
            .queryParam("sourceCurrency", "USD")
            .queryParam("targetCurrency", "EUR")
                .queryParam("amount", amount)
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
    void Should_Return400Error_When_ExternalIntegrationComplainsBadRequest() {
        Mockito.when(currencyConversionService.convert(Mockito.anyString(), Mockito.anyString(), Mockito.any(BigDecimal.class)))
                .thenThrow(new ExchangeRateIntegrationBadRequestException("Bad Request to Swop"));
        given()
                .when()
                .queryParam("sourceCurrency", "USD")
                .queryParam("targetCurrency", "GBP")
                .queryParam("amount", "123")
                .get("/conversion")
                .then()
                .statusCode(400)
                .body("code", equalTo(Response.Status.BAD_REQUEST.name()))
                .body("message", equalTo("Bad request to exchange rate integration"));
    }

    @Test
    void Should_Return400Error_When_ExternalIntegrationReturnsInvalidResponse() {
        Mockito.when(currencyConversionService.convert(Mockito.anyString(), Mockito.anyString(), Mockito.any(BigDecimal.class)))
                .thenThrow(new ExchangeRateIntegrationInvalidResponseException("Invalid response from Swop"));
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
                .body("fields", equalTo(List.of("convertCurrency.sourceCurrency")))
                .body("message", containsStringIgnoringCase("sourceCurrency: must not be null"));
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
                .body("fields", equalTo(List.of("convertCurrency.targetCurrency")))
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
                .body("fields", equalTo(List.of("convertCurrency.amount")))
                .body("message", equalTo("Invalid input parameters: convertCurrency.amount: must not be null"));
    }

    @Test
    void Should_Return400Error_When_CurrencyCodeIsInvalid() {
        given()
                .when()
                .queryParam("sourceCurrency", "USDA")
                .queryParam("targetCurrency", "G")
                .queryParam("amount", "100.50")
                .get("/conversion")
                .then()
                .statusCode(400)
                .body("fields", equalTo(List.of("convertCurrency.sourceCurrency", "convertCurrency.targetCurrency")))
                .body("message", containsStringIgnoringCase("sourceCurrency: size must be between 3 and 3"))
                .body("message", containsStringIgnoringCase("targetCurrency: size must be between 3 and 3"));
    }

    @Test
    void Should_Return404Error_When_RequestingUnknownRoute() {
        given()
                .when()
                .queryParam("sourceCurrency", "USD")
                .queryParam("targetCurrency", "GBP")
                .queryParam("amount", "100.50")
                .get("/wrong-path")
                .then()
                .statusCode(404)
                .body("code", equalTo("NOT_FOUND"))
                .body("message", containsStringIgnoringCase("Resource not found"));
    }

    @Test
    void Should_Return400Error_When_RequestingInvalidSourceCurrency() {
        Mockito.when(currencyConversionService.convert(Mockito.anyString(), Mockito.anyString(), Mockito.any(BigDecimal.class)))
                .thenThrow(new IllegalArgumentException("Bad source currency"));

        given()
                .when()
                .queryParam("sourceCurrency", "USD")
                .queryParam("targetCurrency", "GBP")
                .queryParam("amount", "100.50")
                .get("/conversion")
                .then()
                .statusCode(400)
                .body("code", equalTo("BAD_REQUEST"));
    }

    @Test
    void Should_ReturnValidationError440_When_GivenZeroAsTheAmount() {
        given()
            .when()
            .queryParam("sourceCurrency", "USD")
            .queryParam("targetCurrency", "EUR")
            .queryParam("amount", "0")
            .get("/conversion")
            .then()
            .statusCode(400)
                .body("fields", equalTo(List.of("convertCurrency.amount")))
                .body("message", is("Amount must be positive"));
    }

    @Test
    void Should_ReturnValidationError440_When_GivenNegativeAmount() {
        given()
                .when()
                .queryParam("sourceCurrency", "USD")
                .queryParam("targetCurrency", "EUR")
                .queryParam("amount", "-5")
                .get("/conversion")
                .then()
                .statusCode(400)
                .body("fields", equalTo(List.of("convertCurrency.amount")))
                .body("message", containsStringIgnoringCase("amount: must match \"^[0-9]+(.[0-9]{1,2})?$\""));
    }

    @Test
    void Should_ReturnValidationError440_When_GivenAmountWithMoreThan2Scale() {
        given()
                .when()
                .queryParam("sourceCurrency", "USD")
                .queryParam("targetCurrency", "EUR")
                .queryParam("amount", "100.123")
                .get("/conversion")
                .then()
                .statusCode(400)
                .body("fields", equalTo(List.of("convertCurrency.amount")))
                .body("message", containsStringIgnoringCase("amount: must match \"^[0-9]+(.[0-9]{1,2})?$\""));
    }
    
    @Test
    void Should_ReturnValidationError400_When_GivenAndInvalidSourceCurrency() {
        given()
            .when()
                .queryParam("sourceCurrency", "321")
            .queryParam("targetCurrency", "EUR")
            .queryParam("amount", "100")
            .get("/conversion")
            .then()
            .statusCode(400)
                .body("fields", equalTo(List.of("convertCurrency.sourceCurrency")))
                .body("message", containsStringIgnoringCase("sourceCurrency: must match \"^[A-Z]{3}$\""));
    }

    @Test
    void Should_ReturnValidationError400_When_GivenAndInvalidTargetCurrency() {
        given()
                .when()
                .queryParam("sourceCurrency", "USD")
                .queryParam("targetCurrency", "low")
                .queryParam("amount", "100")
                .get("/conversion")
                .then()
                .statusCode(400)
                .body("fields", equalTo(List.of("convertCurrency.targetCurrency")))
                .body("message", containsStringIgnoringCase("targetCurrency: must match \"^[A-Z]{3}$\""));
    }
} 