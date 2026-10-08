package com.telcox.customerservice.dto;

import com.telcox.customerservice.enums.DocumentType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DocumentResponse {
    private Long id;
    private Long customerId;
    private DocumentType documentType;
    private String fileRef;
    private LocalDateTime verifiedAt;
}
