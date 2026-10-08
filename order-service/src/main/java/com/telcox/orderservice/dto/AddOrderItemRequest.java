package com.telcox.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddOrderItemRequest {
    private String productCode;
    private String productType;
    private Integer quantity;
}
