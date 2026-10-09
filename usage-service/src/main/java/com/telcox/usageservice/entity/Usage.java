package com.telcox.usageservice.entity;

import com.telcox.usageservice.enums.UsageType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "usage_records")
public class Usage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long subscriptionId;
    @Enumerated(EnumType.STRING)
    private UsageType usageType;
    private BigDecimal amount;
    private LocalDateTime usedAt;
}
