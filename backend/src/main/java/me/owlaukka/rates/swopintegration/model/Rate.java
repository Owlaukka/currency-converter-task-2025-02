package me.owlaukka.rates.swopintegration.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Rate(String baseCurrency, String quoteCurrency, BigDecimal quote, LocalDate date) {
}
