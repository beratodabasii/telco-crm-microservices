package com.telcox.orderservice.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class CustomerClient {

    private final RestClient restClient;
    public CustomerClient() {
        this.restClient = RestClient.create("http://" +  "localhost:9002");
    }
    public CustomerResponse getCustomer(Long id){
        return restClient.get()
                .uri("/api/v1/customers/{id}",id)
                .retrieve()
                .body(CustomerResponse.class);
    }
}
