package me.owlaukka.rates;

import java.math.BigDecimal;

public record EuroExchangeRate(String currencyCode, BigDecimal rate) {
}
