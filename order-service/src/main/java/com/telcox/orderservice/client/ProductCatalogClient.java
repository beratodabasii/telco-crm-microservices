package com.telcox.orderservice.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ProductCatalogClient {

    private final RestClient restClient;

    public ProductCatalogClient() {
        this.restClient = RestClient.create("http://" + "localhost:9003");
    }

    public TariffResponse getTariff(String code) {
        return restClient.get()
                .uri("/api/v1/tariffs/{code}", code)
                .retrieve()
                .body(TariffResponse.class);
    }
}