package me.owlaukka.rates;

import java.time.LocalDate;

public record EuroRatesForSourceAndTargetCurrency(EuroExchangeRate sourceRate, EuroExchangeRate targetRate,
                                                  LocalDate dateOfRates) {
}
