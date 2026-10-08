package com.telcox.productcatalogservice.dto;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateTariffPriceRequest {

    private BigDecimal newPrice;
}