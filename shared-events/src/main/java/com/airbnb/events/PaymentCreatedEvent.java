package com.airbnb.events;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCreatedEvent {
    private Long reservationId;
    private String payuOrderId;
}
