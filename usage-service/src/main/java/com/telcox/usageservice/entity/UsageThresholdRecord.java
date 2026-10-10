package com.telcox.usageservice.entity;

import com.telcox.usageservice.enums.UsageThresholdLevel;
import com.telcox.usageservice.enums.UsageType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "usage_threshold_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsageThresholdRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long subscriptionId;

    @Enumerated(EnumType.STRING)
    private UsageType usageType;

    @Enumerated(EnumType.STRING)
    private UsageThresholdLevel thresholdLevel;

    private LocalDateTime createdAt;
}