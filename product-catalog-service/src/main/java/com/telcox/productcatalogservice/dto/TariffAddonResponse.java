package com.telcox.productcatalogservice.dto;

import lombok.Data;

@Data
public class TariffAddonResponse {

    private Long id;
    private Long tariffId;
    private Long addonId;
}