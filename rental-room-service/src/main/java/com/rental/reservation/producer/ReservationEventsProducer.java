package com.rental.reservation.producer;

import com.airbnb.events.ReservationCancelledEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationEventsProducer {

  private final KafkaTemplate<String, ReservationCancelledEvent> reservationCancelledEventKafkaTemplate;
  public void sendCancellationEvent(ReservationCancelledEvent event) {
    reservationCancelledEventKafkaTemplate.send("cancellation-created", event.getReservationId().toString(), event);
    System.out.println("Kafka sent event: " + event);
  }
}
