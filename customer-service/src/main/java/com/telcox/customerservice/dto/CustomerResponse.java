package com.telcox.customerservice.dto;

import com.telcox.customerservice.enums.CustomerStatus;
import com.telcox.customerservice.enums.CustomerType;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CustomerResponse {

    private Long id;
    private CustomerType type;
    private String firstName;
    private String lastName;
    private String identityNumber;
    private LocalDate dateOfBirth;
    private CustomerStatus status;
    private LocalDateTime createdAt;
    private String email;
    private String phoneNumber;
    private boolean deleted;

}
