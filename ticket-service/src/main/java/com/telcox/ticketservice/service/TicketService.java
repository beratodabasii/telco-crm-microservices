package com.telcox.ticketservice.service;

import com.telcox.ticketservice.dto.CreateTicketRequest;
import com.telcox.ticketservice.dto.TicketResponse;
import com.telcox.ticketservice.entity.Ticket;
import com.telcox.ticketservice.enums.TicketStatus;
import com.telcox.ticketservice.exception.TicketNotFoundException;
import com.telcox.ticketservice.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;

    public TicketResponse createTicket(CreateTicketRequest request) {
        Ticket ticket = new Ticket();
        ticket.setCustomerId(request.getCustomerId());
        ticket.setSubject(request.getSubject());
        ticket.setDescription(request.getDescription());
        ticket.setPriority(request.getPriority());
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setUpdatedAt(LocalDateTime.now());
        Ticket savedTicket = ticketRepository.save(ticket);
        return mapToResponse(savedTicket);

    }


    public TicketResponse getTicketById(Long id){
        Ticket ticket = ticketRepository.findById(id)
                        .orElseThrow(()-> new TicketNotFoundException("Ticket not found"));
        return mapToResponse(ticket);
    }


    public List<TicketResponse> getAllTickets(){
      return ticketRepository.findAll()
              .stream()
              .map(this::mapToResponse)
              .toList();
    }


    public TicketResponse updateStatus(Long id, TicketStatus newStatus){
        Ticket ticket =  ticketRepository.findById(id)
                    .orElseThrow(()-> new TicketNotFoundException("Ticket not found"));
        ticket.setStatus(newStatus);
        ticket.setUpdatedAt(LocalDateTime.now());
        Ticket savedTicket = ticketRepository.save(ticket);
        return mapToResponse(savedTicket);
    }


    public List<TicketResponse>  getTicketsByCustomerId(Long customerId){
        return ticketRepository.findByCustomerId(customerId)
                .stream()
                .map(this::mapToResponse)
                .toList();

    }


    private TicketResponse mapToResponse(Ticket ticket) {
        TicketResponse ticketResponse = new TicketResponse();
        ticketResponse.setId(ticket.getId());
        ticketResponse.setCustomerId(ticket.getCustomerId());
        ticketResponse.setSubject(ticket.getSubject());
        ticketResponse.setDescription(ticket.getDescription());
        ticketResponse.setPriority(ticket.getPriority());
        ticketResponse.setStatus(ticket.getStatus());
        ticketResponse.setCreatedAt(ticket.getCreatedAt());
        ticketResponse.setUpdatedAt(ticket.getUpdatedAt());
        return ticketResponse;

    }
}
