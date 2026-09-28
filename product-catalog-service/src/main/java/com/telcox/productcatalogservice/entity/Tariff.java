package com.telcox.productcatalogservice.entity;

import com.telcox.productcatalogservice.enums.TariffStatus;
import com.telcox.productcatalogservice.enums.TariffType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Tariff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private String name;
    @Enumerated(EnumType.STRING)
    private TariffType tariffType;
    private BigDecimal monthlyFee;
    private Integer minutesIncluded;
    private Integer smsIncluded;
    private Integer dataMbIncluded;
    @Enumerated(EnumType.STRING)
    private TariffStatus tariffStatus;
    private LocalDateTime effectiveFrom;
    private LocalDateTime effectiveTo;
    private Integer version;


}
