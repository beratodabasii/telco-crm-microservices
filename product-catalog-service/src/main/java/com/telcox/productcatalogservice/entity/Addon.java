package com.telcox.productcatalogservice.entity;

import com.telcox.productcatalogservice.enums.AddonType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Addon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private String name;
    private BigDecimal price;
    @Enumerated(EnumType.STRING)
    private AddonType addonType;
    private Integer validityDays;

}
