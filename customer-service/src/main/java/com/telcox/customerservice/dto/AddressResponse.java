package com.telcox.customerservice.dto;

import lombok.Data;

@Data
public class AddressResponse {
    private Long id;
    private Long customerId;
    private String line1;
    private String city;
    private String district;
    private String postalCode;
    private boolean defaultAddress;
}
