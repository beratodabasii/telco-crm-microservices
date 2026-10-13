package com.telcox.billingservice.entity;

import com.telcox.billingservice.enums.InvoiceStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long subscriptionId;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;

    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;

    private LocalDateTime createdAt;

}
