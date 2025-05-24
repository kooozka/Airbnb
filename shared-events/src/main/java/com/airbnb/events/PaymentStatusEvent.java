package com.airbnb.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentStatusEvent {
    private Long reservationId;
    private String paymentStatus;
}
