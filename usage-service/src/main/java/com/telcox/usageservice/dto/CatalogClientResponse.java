package com.telcox.usageservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CatalogClientResponse {
    private String code;
    private Integer minutesIncluded;
    private Integer smsIncluded;
    private Integer dataMbIncluded;
}
