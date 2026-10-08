package com.telcox.subscriptionservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateSubscriptionRequest {

    private Long customerId;
    private Long orderId;
    private String tariffCode;

}
