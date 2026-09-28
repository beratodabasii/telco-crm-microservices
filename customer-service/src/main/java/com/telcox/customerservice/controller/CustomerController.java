package com.telcox.customerservice.controller;

import com.telcox.customerservice.entity.Address;
import com.telcox.customerservice.entity.Customer;
import com.telcox.customerservice.entity.Document;
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
    public Customer createCustomer(@RequestBody Customer customer) {
        return customerService.createCustomer(customer);
    }

    @GetMapping("/{id}")
    public Customer getCustomerById(@PathVariable Long id){
        return customerService.getCustomerById(id);
    }

    @GetMapping
    public List<Customer> getAllCustomers(){
        return customerService.getAllCustomers();
    }

    @PutMapping("/{id}")
    public Customer updateCustomer(@PathVariable Long id ,@RequestBody Customer customer){
        return customerService.updateCustomer(id, customer);
    }

    @PostMapping("/{id}/documents")
    public Document addDocument(@PathVariable Long id, @RequestBody Document document){
        return customerService.addDocument(id , document);
    }

    @PostMapping("/{id}/kyc/approve")
    public Customer approveKyc(@PathVariable Long id ){
        return customerService.approveKyc(id);
    }

    @PostMapping("/{id}/addresses")
    public Address addAddress(@PathVariable Long id, @RequestBody Address address){
        return customerService.addAddress(id, address);
    }

    @DeleteMapping("/{id}")
    public Customer softDeleteCustomer(@PathVariable Long id){
        return  customerService.softDeleteCustomer(id);
    }

    @PostMapping("/{id}/kyc/reject")
    public Customer rejectKyc(@PathVariable Long id){
        return customerService.rejectKyc(id);
    }
}
