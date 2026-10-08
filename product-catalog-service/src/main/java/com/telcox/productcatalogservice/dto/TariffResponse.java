package com.telcox.productcatalogservice.dto;

import com.telcox.productcatalogservice.enums.TariffStatus;
import com.telcox.productcatalogservice.enums.TariffType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TariffResponse {

    private Long id;
    private String code;
    private String name;
    private TariffType tariffType;
    private BigDecimal monthlyFee;
    private Integer minutesIncluded;
    private Integer smsIncluded;
    private Integer dataMbIncluded;
    private TariffStatus tariffStatus;
    private LocalDateTime effectiveFrom;
    private LocalDateTime effectiveTo;
    private Integer version;
}