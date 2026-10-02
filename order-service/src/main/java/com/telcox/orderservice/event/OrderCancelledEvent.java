package com.telcox.orderservice.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderCancelledEvent {
    private Long orderId;
    private Long customerId;
    private LocalDateTime cancelledAt;
}
