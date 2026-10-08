package com.telcox.customerservice.controller;

import com.telcox.customerservice.dto.AddAddressRequest;
import com.telcox.customerservice.dto.AddDocumentRequest;
import com.telcox.customerservice.dto.AddressResponse;
import com.telcox.customerservice.dto.CreateCustomerRequest;
import com.telcox.customerservice.dto.CustomerResponse;
import com.telcox.customerservice.dto.DocumentResponse;
import com.telcox.customerservice.dto.UpdateCustomerRequest;
import com.telcox.customerservice.service.CustomerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public CustomerResponse createCustomer(
            @RequestBody CreateCustomerRequest request
    ) {
        return customerService.createCustomer(request);
    }

    @GetMapping("/{id}")
    public CustomerResponse getCustomerById(
            @PathVariable Long id
    ) {
        return customerService.getCustomerById(id);
    }

    @GetMapping
    public List<CustomerResponse> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @PutMapping("/{id}")
    public CustomerResponse updateCustomer(
            @PathVariable Long id,
            @RequestBody UpdateCustomerRequest request
    ) {
        return customerService.updateCustomer(id, request);
    }

    @PostMapping("/{id}/documents")
    public DocumentResponse addDocument(
            @PathVariable Long id,
            @RequestBody AddDocumentRequest request
    ) {
        return customerService.addDocument(id, request);
    }

    @PostMapping("/{id}/kyc/approve")
    public CustomerResponse approveKyc(
            @PathVariable Long id
    ) {
        return customerService.approveKyc(id);
    }

    @PostMapping("/{id}/addresses")
    public AddressResponse addAddress(
            @PathVariable Long id,
            @RequestBody AddAddressRequest request
    ) {
        return customerService.addAddress(id, request);
    }

    @DeleteMapping("/{id}")
    public CustomerResponse softDeleteCustomer(
            @PathVariable Long id
    ) {
        return customerService.softDeleteCustomer(id);
    }

    @PostMapping("/{id}/kyc/reject")
    public CustomerResponse rejectKyc(
            @PathVariable Long id
    ) {
        return customerService.rejectKyc(id);
    }
}