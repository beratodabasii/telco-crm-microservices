package com.telcox.subscriptionservice.dto;

import com.telcox.subscriptionservice.enums.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionResponse {
    private Long id;
    private Long customerId;
    private Long orderId;
    private String tariffCode;
    private SubscriptionStatus status;
    private LocalDateTime startDate;
    private LocalDateTime createdAt;
    private String msisdn;
}
