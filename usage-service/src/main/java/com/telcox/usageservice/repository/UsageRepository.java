package com.telcox.usageservice.repository;

import com.telcox.usageservice.entity.Usage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsageRepository extends JpaRepository<Usage, Long> {
    List<Usage> findBySubscriptionIdOrderByUsedAtDesc(Long subsriptionId);
}
