package com.telcox.customerservice.entity;

import com.telcox.customerservice.enums.CustomerStatus;
import com.telcox.customerservice.enums.CustomerType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private CustomerType type;
    private String firstName;
    private String lastName;
    private String identityNumber;
    private LocalDate dateOfBirth;
    @Enumerated(EnumType.STRING)
    private CustomerStatus status;
    private LocalDateTime createdAt;
    private boolean deleted;
    private String email;
    private String phoneNumber;

}
