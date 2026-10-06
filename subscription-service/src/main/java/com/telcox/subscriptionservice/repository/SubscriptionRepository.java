package com.telcox.subscriptionservice.repository;

import com.telcox.subscriptionservice.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
}
