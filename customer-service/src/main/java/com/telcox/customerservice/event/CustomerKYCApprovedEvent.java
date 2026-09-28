package com.telcox.customerservice.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerKYCApprovedEvent {
    private Long customerId;
    private LocalDateTime approvedAt;
}
