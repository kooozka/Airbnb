package com.payment.paymentservice.producer;

import com.airbnb.events.PaymentCreatedEvent;
import com.airbnb.events.PaymentStatusEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentEventsProducer {

    private final KafkaTemplate<String, PaymentCreatedEvent> paymentCreatedEventKafkaTemplate;
    private final KafkaTemplate<String, PaymentStatusEvent> paymentStatusEventKafkaTemplate;

    public void sendPaymentCreationEvent(PaymentCreatedEvent event) {
        paymentCreatedEventKafkaTemplate.send("payment-created", event.getReservationId().toString(), event);
        System.out.println("Kafka sent event: " + event);
    }

    public void sendPaymentStatusEvent(PaymentStatusEvent event) {
        paymentStatusEventKafkaTemplate.send("payment-status", event.getReservationId().toString(), event);
        System.out.println("Kafka sent event: " + event);
    }
}
