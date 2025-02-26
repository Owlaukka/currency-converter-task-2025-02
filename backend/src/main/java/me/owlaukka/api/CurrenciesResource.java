package me.owlaukka.api;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.common.NotImplementedYet;

@ApplicationScoped
public class CurrenciesResource implements CurrenciesApi {

    @Override
    public Response getSupportedCurrencies() {
        throw new NotImplementedYet();
    }
}
