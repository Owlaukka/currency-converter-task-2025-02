package me.owlaukka.rates;

import jakarta.enterprise.context.ApplicationScoped;
import me.owlaukka.common.Pair;
import org.jboss.resteasy.reactive.common.NotImplementedYet;

@ApplicationScoped
public class ExchangeRateServiceImpl implements ExchangeRateService {

    @Override
    public Pair<EuroExchangeRate, EuroExchangeRate> getEuroRatesForSourceAndTargetCurrency(String sourceCurrency, String targetCurrency) {
        throw new NotImplementedYet();
    }
}
