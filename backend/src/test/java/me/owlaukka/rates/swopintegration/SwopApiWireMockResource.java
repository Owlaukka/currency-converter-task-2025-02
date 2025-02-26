package me.owlaukka.rates.swopintegration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import java.util.Map;

public class SwopApiWireMockResource implements QuarkusTestResourceLifecycleManager {
    private WireMockServer wireMockServer;

    @Override
    public Map<String, String> start() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().dynamicPort());
        wireMockServer.start();

        wireMockServer.stubFor(WireMock.post(WireMock.urlEqualTo("/graphql"))
            .withHeader("Accept", WireMock.equalTo("application/json"))
            .withHeader("Authorization", WireMock.containing("ApiKey"))
            .withHeader("Content-Type", WireMock.containing("application/json"))
            .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                        "data": {
                            "latest": [
                                {
                                    "baseCurrency": "BTC",
                                    "quoteCurrency": "EUR",
                                    "quote": 39000.50,
                                    "date": "2024-02-21"
                                },
                                {
                                    "baseCurrency": "BTC",
                                    "quoteCurrency": "USD",
                                    "quote": 42150.75,
                                    "date": "2024-02-21"
                                }
                            ]
                        }
                    }
                    """)
            ));

        return Map.of("quarkus.smallrye-graphql-client.swop-api.url", wireMockServer.baseUrl() + "/graphql");
    }

    @Override
    public void stop() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }
} 