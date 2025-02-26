package me.owlaukka;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
class CurrencyConversionResourceTest {

    @Test
    void basicConversion() {
        given()
            .when()
            .queryParam("sourceCurrency", "USD")
            .queryParam("targetCurrency", "EUR")
            .queryParam("amount", "100.50")
            .get("/conversion")
            .then()
            .statusCode(200)
            .body("convertedAmount", notNullValue())
            .body("date", notNullValue());
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