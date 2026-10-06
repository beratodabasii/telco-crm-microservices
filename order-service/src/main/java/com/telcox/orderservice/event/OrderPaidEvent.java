package com.telcox.orderservice.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderPaidEvent {
    private Long orderId;
    private Long customerId;
    private String tariffCode;
    private LocalDateTime paidAt;
}
