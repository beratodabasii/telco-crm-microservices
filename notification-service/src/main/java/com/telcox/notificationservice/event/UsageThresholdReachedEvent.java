package com.telcox.notificationservice.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsageThresholdReachedEvent {
    private Long subscriptionId;
    private String usageType;
    private String thresholdLevel;
    private BigDecimal percentage;
    private LocalDateTime occurredAt;
}
