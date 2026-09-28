package com.telcox.customerservice.repository;

import com.telcox.customerservice.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByCustomerId(Long customerId);

}
