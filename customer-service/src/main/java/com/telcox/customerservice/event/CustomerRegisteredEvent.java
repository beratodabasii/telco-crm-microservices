package com.telcox.customerservice.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerRegisteredEvent {

    private Long customerId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;


}
