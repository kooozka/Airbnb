package com.rental.reservation.service;

import org.springframework.stereotype.Component;

@Component
public class RefundServiceClient {
  public void refundGuest(Long reservationId) {
    // TODO: refund
    System.out.println("Refunding base amount for reservation: " + reservationId);
  }

  public void refundAdditionalCompensation(Long reservationId) {
    // TODO: refund additional 15%
    System.out.println("Refunding additional 15% for reservation: " + reservationId);
  }
}
