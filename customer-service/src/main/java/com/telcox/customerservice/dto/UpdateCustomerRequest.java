package com.telcox.customerservice.dto;

import com.telcox.customerservice.enums.CustomerType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateCustomerRequest {
    private CustomerType type;
    private String firstName;
    private String lastName;
    private String identityNumber;
    private LocalDate dateOfBirth;
    private String email;
    private String phoneNumber;
}
