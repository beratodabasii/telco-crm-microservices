package com.telcox.subscriptionservice.entity;

import com.telcox.subscriptionservice.enums.MsisdnStatus;
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
@Table(name = "msisdn_pool")
public class MsisdnPool {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String msisdn;
    @Enumerated(EnumType.STRING)
    private MsisdnStatus status;
    private LocalDateTime reservedUntil;
}
