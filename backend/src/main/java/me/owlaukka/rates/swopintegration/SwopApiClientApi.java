package me.owlaukka.rates.swopintegration;

import io.smallrye.graphql.client.typesafe.api.GraphQLClientApi;
import me.owlaukka.rates.swopintegration.model.Rate;

import org.eclipse.microprofile.graphql.NonNull;

import java.util.List;

@GraphQLClientApi(configKey = "swop-api")
public interface SwopApiClientApi {

    List<Rate> latest(List<@NonNull String> quoteCurrencies);
}
