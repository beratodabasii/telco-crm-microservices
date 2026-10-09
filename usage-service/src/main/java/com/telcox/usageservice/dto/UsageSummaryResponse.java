package com.telcox.usageservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsageSummaryResponse {
    private Long subscriptionId;
    private BigDecimal dataUsed;
    private BigDecimal voiceUsed;
    private BigDecimal smsUsed;
}
