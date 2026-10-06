package com.telcox.orderservice.repository;

import com.telcox.orderservice.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long orderId);
    Optional<OrderItem> findFirstByOrderId(Long orderId);

}
