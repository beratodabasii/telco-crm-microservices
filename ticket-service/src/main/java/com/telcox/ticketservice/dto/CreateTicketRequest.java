package com.telcox.ticketservice.dto;

import com.telcox.ticketservice.enums.TicketPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateTicketRequest {
    @NotNull
    private Long customerId;
    @NotBlank
    private String subject;
    @NotNull
    private String description;
    @NotNull
    private TicketPriority priority;
}
