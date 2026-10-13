package com.telcox.billingservice.controller;

import com.telcox.billingservice.dto.CreateInvoiceRequest;
import com.telcox.billingservice.dto.InvoiceResponse;
import com.telcox.billingservice.service.BillingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
public class BillingController {
    private final BillingService billingService;


    @PostMapping
    public InvoiceResponse createInvoice(
            @RequestBody CreateInvoiceRequest request) {

        return billingService.createInvoice(request);
    }

    @PutMapping("/{id}/issue")
    public InvoiceResponse issueInvoice(@PathVariable Long id) {
        return billingService.issueInvoice(id);
    }

    @PutMapping("/{id}/pay")
    public InvoiceResponse markAsPaid(@PathVariable Long id) {
        return billingService.markAsPaid(id);
    }

    @GetMapping("/{id}")
    public InvoiceResponse getInvoiceById(@PathVariable Long id) {
        return billingService.getInvoiceById(id);
    }
}
