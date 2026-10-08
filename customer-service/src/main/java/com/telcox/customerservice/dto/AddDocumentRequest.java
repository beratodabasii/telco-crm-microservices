package com.telcox.customerservice.dto;

import com.telcox.customerservice.enums.DocumentType;
import lombok.Data;

@Data
public class AddDocumentRequest {
    private DocumentType documentType;
    private String fileRef;
}
