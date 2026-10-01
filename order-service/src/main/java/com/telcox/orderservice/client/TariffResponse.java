package com.telcox.orderservice.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TariffResponse {

    private String code;
    private BigDecimal monthlyFee;
    private Integer version;

}
