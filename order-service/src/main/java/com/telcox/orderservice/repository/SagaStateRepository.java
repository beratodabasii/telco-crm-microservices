package com.telcox.orderservice.repository;

import com.telcox.orderservice.entity.SagaState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SagaStateRepository extends JpaRepository<SagaState, Long> {
}
