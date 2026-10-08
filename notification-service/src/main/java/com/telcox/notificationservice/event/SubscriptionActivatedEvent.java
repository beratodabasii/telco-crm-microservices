package com.telcox.notificationservice.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionActivatedEvent {

    private Long subscriptionId;
    private Long customerId;
    private Long orderId;
    private String tariffCode;
    private String msisdn;
    private LocalDateTime activatedAt;


}
