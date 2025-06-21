package com.rental.reservation.service;

import com.airbnb.events.UserNotificationEvent;
import com.rental.reservation.producer.ReservationEventsProducer;
import com.rental.room.model.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RecommendationServiceClient {
  private final ReservationEventsProducer producer;
  public void sendRecommendationEmail(String guestEmail, List<Room> similarOffers) {
    System.out.println("Sending similar offers recommendations to guest with email: " + guestEmail);
    UserNotificationEvent event = new UserNotificationEvent(
        guestEmail,
        "Podobne oferty",
        "Niestety właściciel obiektu odwołał rezerwację. Na szczęście na naszej stronie jest wiele innych obiektów, które możesz zarezerwować w tym terminie: " +
            similarOffers.stream()
                .map(Room::getTitle)
                .collect(Collectors.joining(", "))
    );

    producer.sendNotification(event);
  }
}