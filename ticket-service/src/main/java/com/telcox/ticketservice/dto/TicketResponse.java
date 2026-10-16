package com.telcox.ticketservice.dto;

import com.telcox.ticketservice.enums.TicketPriority;
import com.telcox.ticketservice.enums.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TicketResponse {
    private Long id;
    private Long customerId;
    private String subject;
    private String description;
    private TicketStatus status;
    private TicketPriority priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
