package com.rental.reservation.service;

import org.springframework.stereotype.Component;

@Component
public class RecommendationServiceClient {
  public void sendRecommendationEmail(String guestEmail) {
    // TODO: find other similar offers and send to guest
    System.out.println("Sending similar offers recommendations to guest with email: " + guestEmail);
  }
}