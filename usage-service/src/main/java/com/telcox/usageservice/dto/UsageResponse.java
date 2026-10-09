package com.telcox.usageservice.dto;

import com.telcox.usageservice.enums.UsageType;
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
public class UsageResponse {
    private Long id;
    private Long subscriptionId;
    private UsageType usageType;
    private BigDecimal amount;
    private LocalDateTime usedAt;
}
