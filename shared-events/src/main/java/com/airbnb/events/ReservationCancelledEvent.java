package com.airbnb.events;

import com.airbnb.enums.CancellerType;
import lombok.Data;

import java.time.Instant;

@Data
public class ReservationCancelledEvent {
  private Long reservationId;
  private Instant cancelledAt;
  private CancellerType cancelledBy;

  public ReservationCancelledEvent(Long id, String cancelledBy){
    this.reservationId = id;
    this.cancelledAt = Instant.now();
    this.cancelledBy = CancellerType.valueOf(cancelledBy);
  }
}