package com.airbnb.events;

import com.airbnb.enums.CancellerType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationCancelledEvent {
  private Long reservationId;
  private String cancelledBy;
  private String payuOrderId;
  private double amount;

}