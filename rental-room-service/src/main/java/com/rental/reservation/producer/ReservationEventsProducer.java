package com.rental.reservation.producer;

import com.airbnb.events.ReservationCancelledEvent;
import com.airbnb.events.ReservationCreatedEvent;
import com.airbnb.events.UserNotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationEventsProducer {

  private final KafkaTemplate<String, ReservationCancelledEvent> reservationCancelledEventKafkaTemplate;
  private final KafkaTemplate<String, ReservationCreatedEvent> reservationCreatedEventKafkaTemplate;
  private final KafkaTemplate<String, UserNotificationEvent> notificationKafkaTemplate;

  public void sendCancellationEvent(ReservationCancelledEvent event) {
    reservationCancelledEventKafkaTemplate.send("reservation-cancelled", event.getReservationId().toString(), event);
    System.out.println("Kafka sent event: " + event);
  }

  public void sendReservationCreatedEvent(ReservationCreatedEvent event) {
    reservationCreatedEventKafkaTemplate.send("reservation-created", event.getReservationId().toString(), event);
    System.out.println("Kafka sent event: " + event);
  }
  public void sendNotification(UserNotificationEvent event) {
      notificationKafkaTemplate.send("user-notifications", event);
    }

}
