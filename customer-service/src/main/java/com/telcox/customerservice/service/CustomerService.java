package com.telcox.customerservice.service;

import com.telcox.customerservice.dto.*;
import com.telcox.customerservice.entity.Address;
import com.telcox.customerservice.entity.Customer;
import com.telcox.customerservice.entity.Document;
import com.telcox.customerservice.enums.CustomerStatus;
import com.telcox.customerservice.event.CustomerKYCApprovedEvent;
import com.telcox.customerservice.event.CustomerRegisteredEvent;
import com.telcox.customerservice.event.CustomerUpdatedEvent;
import com.telcox.customerservice.exception.CustomerNotFoundException;
import com.telcox.customerservice.exception.InvalidIdentityNumberException;
import com.telcox.customerservice.exception.KycDocumentNotFoundException;
import com.telcox.customerservice.outbox.OutboxEvent;
import com.telcox.customerservice.repository.AddressRepository;
import com.telcox.customerservice.repository.CustomerRepository;
import com.telcox.customerservice.repository.DocumentRepository;
import com.telcox.customerservice.repository.OutboxEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;


import java.time.LocalDateTime;
import java.util.List;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final DocumentRepository documentRepository;
    private final AddressRepository addressRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;
    public CustomerService(CustomerRepository customerRepository ,
                           DocumentRepository documentRepository ,
                           AddressRepository addressRepository,
                           OutboxEventRepository outboxEventRepository,
                           ObjectMapper objectMapper
                           ) {
        this.customerRepository = customerRepository;
        this.documentRepository = documentRepository;
        this.addressRepository = addressRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
    }
    @Transactional
    public CustomerResponse createCustomer(CreateCustomerRequest request) {

        String identityNumber = request.getIdentityNumber();

        if (identityNumber == null ||
                !identityNumber.matches("\\d{11}") ||
                identityNumber.charAt(0) == '0') {

            throw new InvalidIdentityNumberException("Geçersiz kimlik numarası");
        }

        int firstDigit = Character.getNumericValue(identityNumber.charAt(0));
        int secondDigit = Character.getNumericValue(identityNumber.charAt(1));
        int thirdDigit = Character.getNumericValue(identityNumber.charAt(2));
        int fourthDigit = Character.getNumericValue(identityNumber.charAt(3));
        int fifthDigit = Character.getNumericValue(identityNumber.charAt(4));
        int sixthDigit = Character.getNumericValue(identityNumber.charAt(5));
        int seventhDigit = Character.getNumericValue(identityNumber.charAt(6));
        int eighthDigit = Character.getNumericValue(identityNumber.charAt(7));
        int ninthDigit = Character.getNumericValue(identityNumber.charAt(8));

        int oddSum =
                firstDigit +
                        thirdDigit +
                        fifthDigit +
                        seventhDigit +
                        ninthDigit;

        int evenSum =
                secondDigit +
                        fourthDigit +
                        sixthDigit +
                        eighthDigit;

        int calculatedTenthDigit = ((oddSum * 7) - evenSum) % 10;
        int tenthDigit = Character.getNumericValue(identityNumber.charAt(9));

        if (tenthDigit != calculatedTenthDigit) {
            throw new InvalidIdentityNumberException("Geçersiz kimlik numarası");
        }

        int eleventhDigit =
                Character.getNumericValue(identityNumber.charAt(10));

        int firstTenDigitsSum =
                firstDigit +
                        secondDigit +
                        thirdDigit +
                        fourthDigit +
                        fifthDigit +
                        sixthDigit +
                        seventhDigit +
                        eighthDigit +
                        ninthDigit +
                        tenthDigit;

        int calculatedEleventhDigit = firstTenDigitsSum % 10;

        if (calculatedEleventhDigit != eleventhDigit) {
            throw new InvalidIdentityNumberException("Geçersiz kimlik numarası");
        }

        Customer newCustomer = new Customer();

        newCustomer.setStatus(CustomerStatus.PENDING);
        newCustomer.setFirstName(request.getFirstName());
        newCustomer.setLastName(request.getLastName());
        newCustomer.setIdentityNumber(request.getIdentityNumber());
        newCustomer.setDateOfBirth(request.getDateOfBirth());
        newCustomer.setType(request.getType());
        newCustomer.setCreatedAt(LocalDateTime.now());
        newCustomer.setDeleted(false);
        newCustomer.setEmail(request.getEmail());
        newCustomer.setPhoneNumber(request.getPhoneNumber());

        Customer savedCustomer =
                customerRepository.save(newCustomer);

        CustomerRegisteredEvent event =
                new CustomerRegisteredEvent(
                        savedCustomer.getId(),
                        savedCustomer.getFirstName(),
                        savedCustomer.getLastName(),
                        savedCustomer.getEmail(),
                        savedCustomer.getPhoneNumber()
                );

        String payload =
                objectMapper.writeValueAsString(event);

        OutboxEvent outboxEvent = new OutboxEvent();

        outboxEvent.setEventType(
                CustomerRegisteredEvent.class.getSimpleName()
        );

        outboxEvent.setPayload(payload);
        outboxEvent.setCreatedAt(LocalDateTime.now());
        outboxEvent.setPublished(false);

        outboxEventRepository.save(outboxEvent);

        return mapToResponse(savedCustomer);
    }

    public CustomerResponse getCustomerById(Long id) {

        Customer customer =
                customerRepository.findByIdAndDeleted(id, false)
                        .orElseThrow(
                                () -> new CustomerNotFoundException(
                                        "Müşteri bulunamadı"
                                )
                        );

        return mapToResponse(customer);
    }

    public List<CustomerResponse> getAllCustomers() {

        return customerRepository.findByDeleted(false)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public CustomerResponse updateCustomer(
            Long id,
            UpdateCustomerRequest request
    ) {

        Customer updatedCustomer =
                customerRepository.findByIdAndDeleted(id, false)
                        .orElseThrow(
                                () -> new CustomerNotFoundException(
                                        "Müşteri bulunamadı"
                                )
                        );

        updatedCustomer.setFirstName(request.getFirstName());
        updatedCustomer.setLastName(request.getLastName());
        updatedCustomer.setIdentityNumber(request.getIdentityNumber());
        updatedCustomer.setDateOfBirth(request.getDateOfBirth());
        updatedCustomer.setType(request.getType());
        updatedCustomer.setEmail(request.getEmail());
        updatedCustomer.setPhoneNumber(request.getPhoneNumber());

        Customer savedCustomer =
                customerRepository.save(updatedCustomer);

        CustomerUpdatedEvent event =
                new CustomerUpdatedEvent();

        event.setCustomerId(savedCustomer.getId());
        event.setFirstName(savedCustomer.getFirstName());
        event.setLastName(savedCustomer.getLastName());
        event.setEmail(savedCustomer.getEmail());
        event.setPhoneNumber(savedCustomer.getPhoneNumber());
        event.setUpdatedAt(LocalDateTime.now());

        String payload =
                objectMapper.writeValueAsString(event);

        OutboxEvent outboxEvent =
                new OutboxEvent();

        outboxEvent.setEventType(
                CustomerUpdatedEvent.class.getSimpleName()
        );

        outboxEvent.setPayload(payload);
        outboxEvent.setCreatedAt(LocalDateTime.now());
        outboxEvent.setPublished(false);

        outboxEventRepository.save(outboxEvent);

        return mapToResponse(savedCustomer);
    }

    public DocumentResponse addDocument(
            Long id,
            AddDocumentRequest request
    ) {

        Customer customer =
                customerRepository.findByIdAndDeleted(id, false)
                        .orElseThrow(
                                () -> new CustomerNotFoundException(
                                        "Müşteri bulunamadı"
                                )
                        );

        Document newDocument =
                new Document();

        newDocument.setCustomerId(customer.getId());
        newDocument.setDocumentType(request.getDocumentType());
        newDocument.setFileRef(request.getFileRef());

        Document savedDocument =
                documentRepository.save(newDocument);

        return mapDocumentToResponse(savedDocument);
    }

    @Transactional
    public CustomerResponse approveKyc(Long id) {

        Customer customer =
                customerRepository.findByIdAndDeleted(id, false)
                        .orElseThrow(
                                () -> new CustomerNotFoundException(
                                        "Müşteri bulunamadı"
                                )
                        );

        List<Document> documents =
                documentRepository.findByCustomerId(customer.getId());

        if (documents.isEmpty()) {
            throw new KycDocumentNotFoundException(
                    "Doküman bulunamadı"
            );
        }

        for (Document document : documents) {
            document.setVerifiedAt(LocalDateTime.now());
        }

        documentRepository.saveAll(documents);

        customer.setStatus(CustomerStatus.ACTIVE);

        Customer customerSaved =
                customerRepository.save(customer);

        CustomerKYCApprovedEvent event =
                new CustomerKYCApprovedEvent();

        event.setCustomerId(customerSaved.getId());
        event.setApprovedAt(LocalDateTime.now());

        String payload =
                objectMapper.writeValueAsString(event);

        OutboxEvent outboxEvent =
                new OutboxEvent();

        outboxEvent.setEventType(
                CustomerKYCApprovedEvent.class.getSimpleName()
        );

        outboxEvent.setPayload(payload);
        outboxEvent.setCreatedAt(LocalDateTime.now());
        outboxEvent.setPublished(false);

        outboxEventRepository.save(outboxEvent);

        return mapToResponse(customerSaved);
    }

    public AddressResponse addAddress(
            Long id,
            AddAddressRequest request
    ) {

        Customer customer =
                customerRepository.findByIdAndDeleted(id, false)
                        .orElseThrow(
                                () -> new CustomerNotFoundException(
                                        "Müşteri bulunamadı"
                                )
                        );

        Address newAddress =
                new Address();

        newAddress.setCustomerId(customer.getId());
        newAddress.setLine1(request.getLine1());
        newAddress.setCity(request.getCity());
        newAddress.setDistrict(request.getDistrict());
        newAddress.setPostalCode(request.getPostalCode());
        newAddress.setDefaultAddress(request.isDefaultAddress());

        Address savedAddress =
                addressRepository.save(newAddress);

        return mapAddressToResponse(savedAddress);
    }

    public CustomerResponse softDeleteCustomer(Long id) {

        Customer customer =
                customerRepository.findByIdAndDeleted(id, false)
                        .orElseThrow(
                                () -> new CustomerNotFoundException(
                                        "Müşteri bulunamadı"
                                )
                        );

        customer.setDeleted(true);

        Customer savedCustomer =
                customerRepository.save(customer);

        return mapToResponse(savedCustomer);
    }

    public CustomerResponse rejectKyc(Long id) {

        Customer customer =
                customerRepository.findByIdAndDeleted(id, false)
                        .orElseThrow(
                                () -> new CustomerNotFoundException(
                                        "Müşteri bulunamadı"
                                )
                        );

        customer.setStatus(CustomerStatus.REJECTED);

        Customer savedCustomer =
                customerRepository.save(customer);

        return mapToResponse(savedCustomer);
    }

    private CustomerResponse mapToResponse(Customer customer) {

        CustomerResponse response =
                new CustomerResponse();

        response.setId(customer.getId());
        response.setType(customer.getType());
        response.setFirstName(customer.getFirstName());
        response.setLastName(customer.getLastName());
        response.setIdentityNumber(customer.getIdentityNumber());
        response.setDateOfBirth(customer.getDateOfBirth());
        response.setStatus(customer.getStatus());
        response.setCreatedAt(customer.getCreatedAt());
        response.setEmail(customer.getEmail());
        response.setPhoneNumber(customer.getPhoneNumber());
        response.setDeleted(customer.isDeleted());

        return response;
    }

    private DocumentResponse mapDocumentToResponse(
            Document document
    ) {

        DocumentResponse response =
                new DocumentResponse();

        response.setId(document.getId());
        response.setCustomerId(document.getCustomerId());
        response.setDocumentType(document.getDocumentType());
        response.setFileRef(document.getFileRef());
        response.setVerifiedAt(document.getVerifiedAt());

        return response;
    }

    private AddressResponse mapAddressToResponse(
            Address address
    ) {

        AddressResponse response =
                new AddressResponse();

        response.setId(address.getId());
        response.setCustomerId(address.getCustomerId());
        response.setLine1(address.getLine1());
        response.setCity(address.getCity());
        response.setDistrict(address.getDistrict());
        response.setPostalCode(address.getPostalCode());
        response.setDefaultAddress(address.isDefaultAddress());

        return response;
    }
}
