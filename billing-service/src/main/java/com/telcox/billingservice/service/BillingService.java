package com.telcox.billingservice.service;

import com.telcox.billingservice.client.SubscriptionClient;
import com.telcox.billingservice.dto.*;
import com.telcox.billingservice.entity.Invoice;
import com.telcox.billingservice.entity.OutboxEvent;
import com.telcox.billingservice.enums.InvoiceStatus;
import com.telcox.billingservice.event.InvoiceIssuedEvent;
import com.telcox.billingservice.event.InvoicePaidEvent;
import com.telcox.billingservice.exception.InvalidInvoiceStateException;
import com.telcox.billingservice.repository.InvoiceRepository;
import com.telcox.billingservice.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BillingService {
    private final InvoiceRepository invoiceRepository;
    private final SubscriptionClient subscriptionClient;
    private final CatalogClient catalogClient;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;


    public InvoiceResponse createInvoice(CreateInvoiceRequest request) {
        SubscriptionClientResponse subscription =
                subscriptionClient.getSubscriptionById(request.getSubscriptionId());

        CatalogClientResponse tariff =
                catalogClient.getTariffByCode(subscription.getTariffCode());

        Invoice invoice = new Invoice();
        invoice.setSubscriptionId(request.getSubscriptionId());
        invoice.setPeriodStart(request.getPeriodStart());
        invoice.setPeriodEnd(request.getPeriodEnd());

        invoice.setAmount(tariff.getMonthlyFee());
        invoice.setStatus(InvoiceStatus.DRAFT);
        invoice.setCreatedAt(LocalDateTime.now());

        Invoice savedInvoice = invoiceRepository.save(invoice);

        return mapToResponse(savedInvoice);


    }

    public InvoiceResponse issueInvoice(Long invoiceId) {

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        if (invoice.getStatus() != InvoiceStatus.DRAFT) {
            throw new InvalidInvoiceStateException(
                    "Only DRAFT invoices can be issued"
            );
        }

        invoice.setStatus(InvoiceStatus.ISSUED);

        Invoice savedInvoice = invoiceRepository.save(invoice);

        InvoiceIssuedEvent event = new InvoiceIssuedEvent(
                savedInvoice.getId(),
                savedInvoice.getSubscriptionId(),
                savedInvoice.getAmount(),
                LocalDateTime.now()
        );
        String payload = toJson(event);

        OutboxEvent outboxEvent = new OutboxEvent();

        outboxEvent.setAggregateType("INVOICE");
        outboxEvent.setAggregateId(savedInvoice.getId());
        outboxEvent.setEventType("InvoiceIssued");
        outboxEvent.setPayload(payload);
        outboxEvent.setCreatedAt(LocalDateTime.now());
        outboxEvent.setPublished(false);

        outboxEventRepository.save(outboxEvent);
        return mapToResponse(savedInvoice);
    }

    public InvoiceResponse markAsPaid(Long invoiceId) {

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        if (invoice.getStatus() != InvoiceStatus.ISSUED) {
            throw new InvalidInvoiceStateException(
                    "Only ISSUED invoices can be paid"
            );
        }

        invoice.setStatus(InvoiceStatus.PAID);

        Invoice savedInvoice = invoiceRepository.save(invoice);
        InvoicePaidEvent event = new InvoicePaidEvent(
                savedInvoice.getId(),
                savedInvoice.getSubscriptionId(),
                savedInvoice.getAmount(),
                LocalDateTime.now()
        );

        String payload = toJson(event);

        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setAggregateType("INVOICE");
        outboxEvent.setAggregateId(savedInvoice.getId());
        outboxEvent.setEventType("InvoicePaid");
        outboxEvent.setPayload(payload);
        outboxEvent.setCreatedAt(LocalDateTime.now());
        outboxEvent.setPublished(false);

        outboxEventRepository.save(outboxEvent);

        return mapToResponse(savedInvoice);
    }

    public InvoiceResponse getInvoiceById(Long invoiceId) {

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        return mapToResponse(invoice);
    }

    private InvoiceResponse mapToResponse(Invoice invoice){
        InvoiceResponse response = new InvoiceResponse();
        response.setId(invoice.getId());
        response.setSubscriptionId(invoice.getSubscriptionId());
        response.setAmount(invoice.getAmount());
        response.setStatus(invoice.getStatus());
        response.setPeriodStart(invoice.getPeriodStart());
        response.setPeriodEnd(invoice.getPeriodEnd());
        response.setCreatedAt(invoice.getCreatedAt());

        return response;

    }
    private String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JacksonException exception) {
            throw new RuntimeException("Event could not be converted to JSON", exception);
        }
    }
}
