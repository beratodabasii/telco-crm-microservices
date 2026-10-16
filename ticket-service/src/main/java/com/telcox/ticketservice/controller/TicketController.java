package com.telcox.ticketservice.controller;

import com.telcox.ticketservice.dto.CreateTicketRequest;
import com.telcox.ticketservice.dto.TicketResponse;
import com.telcox.ticketservice.enums.TicketStatus;
import com.telcox.ticketservice.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;


    @PostMapping
    public TicketResponse createTicket(@Valid @RequestBody CreateTicketRequest request){
        return ticketService.createTicket(request);
    }

    @GetMapping("/{id}")
    public TicketResponse getTicketById(@PathVariable Long id){
        return ticketService.getTicketById(id);
    }


    @GetMapping
    public List<TicketResponse> getTickets(){
        return ticketService.getAllTickets();
    }

    @PutMapping("/{id}/status")
    public TicketResponse updateStatus(@PathVariable Long id, @RequestBody TicketStatus newStatus){
        return ticketService.updateStatus(id, newStatus);
    }

    @GetMapping("/customer/{customerId}")
    public List<TicketResponse> getTicketsByCustomerId(@PathVariable Long customerId){
        return ticketService.getTicketsByCustomerId(customerId);
    }

}
