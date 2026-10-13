package com.telcox.billingservice.event;
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
public class InvoicePaidEvent {
    private Long invoiceId;
    private Long subscriptionId;
    private BigDecimal amount;
    private LocalDateTime occurredAt;
}
