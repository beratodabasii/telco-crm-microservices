package com.telcox.customerservice.dto;

import lombok.Data;

@Data
public class AddAddressRequest {
    private String line1;
    private String city;
    private String district;
    private String postalCode;
    private boolean defaultAddress;
}
