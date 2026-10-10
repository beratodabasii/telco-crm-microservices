package com.telcox.usageservice.event;

import com.telcox.usageservice.enums.UsageThresholdLevel;
import com.telcox.usageservice.enums.UsageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsageThresholdReachedEvent {
    private Long subscriptionId;
    private UsageType usageType;
    private UsageThresholdLevel thresholdLevel;
    private BigDecimal percentage;
    private LocalDateTime occurredAt;
}
