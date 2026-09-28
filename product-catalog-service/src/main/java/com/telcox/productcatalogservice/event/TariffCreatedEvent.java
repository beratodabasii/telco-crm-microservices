package com.telcox.productcatalogservice.event;

import com.telcox.productcatalogservice.enums.TariffStatus;
import com.telcox.productcatalogservice.enums.TariffType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TariffCreatedEvent {
    private Long tariffId;
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
}
