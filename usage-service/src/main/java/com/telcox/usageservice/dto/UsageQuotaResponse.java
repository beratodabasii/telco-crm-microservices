package com.telcox.usageservice.dto;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UsageQuotaResponse {
    private Long subscriptionId;

    private BigDecimal dataUsed;
    private Integer dataLimit;
    private BigDecimal dataPercentage;

    private BigDecimal voiceUsed;
    private Integer voiceLimit;
    private BigDecimal voicePercentage;

    private BigDecimal smsUsed;
    private Integer smsLimit;
    private BigDecimal smsPercentage;
}
