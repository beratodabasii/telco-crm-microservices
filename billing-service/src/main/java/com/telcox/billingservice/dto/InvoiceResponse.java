package com.telcox.billingservice.dto;

import com.telcox.billingservice.enums.InvoiceStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InvoiceResponse {
    private Long id;
    private Long subscriptionId;
    private BigDecimal amount;
    private InvoiceStatus status;
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    private LocalDateTime createdAt;
}
