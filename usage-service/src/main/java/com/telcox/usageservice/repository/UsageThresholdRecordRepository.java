package com.telcox.usageservice.repository;

import com.telcox.usageservice.entity.UsageThresholdRecord;
import com.telcox.usageservice.enums.UsageThresholdLevel;
import com.telcox.usageservice.enums.UsageType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsageThresholdRecordRepository extends JpaRepository<UsageThresholdRecord, Long> {
    boolean existsBySubscriptionIdAndUsageTypeAndThresholdLevel(
            Long subscriptionId,
            UsageType usageType,
            UsageThresholdLevel thresholdLevel
    );
}
