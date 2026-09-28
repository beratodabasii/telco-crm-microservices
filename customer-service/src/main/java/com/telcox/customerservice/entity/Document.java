package com.telcox.customerservice.entity;

import com.telcox.customerservice.enums.DocumentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long customerId;
    @Enumerated(EnumType.STRING)
    private DocumentType documentType;
    private String fileRef;
    private LocalDateTime verifiedAt;


}
