package com.telcox.customerservice.repository;

import com.telcox.customerservice.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByIdAndDeleted(Long id, boolean deleted);
    List<Customer> findByDeleted(boolean deleted);

}
