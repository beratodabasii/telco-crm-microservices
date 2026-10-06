package com.telcox.subscriptionservice.entity;

import com.telcox.subscriptionservice.enums.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "subscriptions")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;
    private Long customerId;
    private Long orderId;
    private String tariffCode;
    private LocalDateTime startDate;
    private LocalDateTime createdAt;
}
