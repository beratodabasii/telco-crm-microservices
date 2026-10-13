package com.telcox.billingservice.dto;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateInvoiceRequest {
    private Long subscriptionId;
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
}
