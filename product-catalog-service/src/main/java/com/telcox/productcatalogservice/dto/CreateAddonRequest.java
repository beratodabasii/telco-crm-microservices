package com.telcox.productcatalogservice.dto;

import com.telcox.productcatalogservice.enums.AddonType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateAddonRequest {

    private String code;
    private String name;
    private BigDecimal price;
    private AddonType addonType;
    private Integer validityDays;
}
