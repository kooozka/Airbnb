package com.rental.reservation.service;

import com.airbnb.enums.CancellerType;
import com.airbnb.events.ReservationCancelledEvent;
import com.rental.reservation.model.Reservation;
import com.rental.reservation.producer.ReservationEventsProducer;
import org.springframework.stereotype.Component;

@Component
public class RefundServiceClient {

  private final ReservationEventsProducer producer;

    public RefundServiceClient(final ReservationEventsProducer producer) {
        this.producer = producer;
    }

    public void refundGuest(Reservation reservation) {
      ReservationCancelledEvent event = new ReservationCancelledEvent(reservation.getId(), CancellerType.GUEST.toString(), reservation.getPayuOrderId(), reservation.getTotalPrice());
      producer.sendCancellationEvent(event);
      System.out.println("Refunding base amount for reservation: " + reservation.getId());
  }

  public void refundAdditionalCompensation(Reservation reservation) {
    ReservationCancelledEvent event = new ReservationCancelledEvent(reservation.getId(), CancellerType.OWNER.toString(), reservation.getPayuOrderId(), reservation.getTotalPrice());
    producer.sendCancellationEvent(event);
    System.out.println("Refunding additional 15% for reservation: " + reservation.getId());
  }
}
