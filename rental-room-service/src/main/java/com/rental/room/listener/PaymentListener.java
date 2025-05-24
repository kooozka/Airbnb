package com.rental.room.listener;

import com.airbnb.events.PaymentCreatedEvent;
import com.airbnb.events.PaymentStatusEvent;
import com.rental.reservation.model.Reservation;
import com.rental.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.rental.reservation.enums.ReservationStatus.COMPLETED;

@Component
@RequiredArgsConstructor
public class PaymentListener {

    private final ReservationRepository reservationRepository;

    @KafkaListener(topics = "payment-created", groupId = "rental-room-group")
    public void handlePaymentCreatedEvent(PaymentCreatedEvent event) {
        Reservation reservation = reservationRepository.findById(event.getReservationId())
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        reservation.setPayuOrderId(event.getPayuOrderId());
        reservationRepository.save(reservation);
        System.out.println("Received and saved payuOrderId to reservation: " + reservation.getId());
    }

    @KafkaListener(topics = "payment-status", groupId = "rental-room-group")
    public void handlePaymentStatusEvent(PaymentStatusEvent event) {
        Reservation reservation = reservationRepository.findById(event.getReservationId())
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        if (event.getPaymentStatus().equals(COMPLETED.toString())) {
            reservation.setStatus(COMPLETED.toString());
        }

        reservationRepository.save(reservation);
        System.out.println("Received and saved payment status to reservation: " + reservation.getId());
    }
}
