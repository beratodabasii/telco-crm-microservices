package com.telcox.usageservice.client;

import com.telcox.usageservice.dto.CatalogClientResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class CatalogClient {
    private final RestClient restClient = RestClient.create("http://localhost:9003");

    public CatalogClientResponse getTariffByCode(String tariffCode){
        return restClient.get()
                .uri("/api/v1/tariffs/{code}", tariffCode)
                .retrieve()
                .body(CatalogClientResponse.class);
    }
}
