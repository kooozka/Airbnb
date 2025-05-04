package com.rental.reservation.service;

import com.rental.reservation.enums.CancellerType;
import com.rental.reservation.enums.ReservationStatus;
import com.rental.reservation.model.Reservation;
import com.rental.reservation.producer.ReservationEventsProducer;
import com.airbnb.events.ReservationCreatedEvent;
import com.rental.room.model.Room;
import com.rental.reservation.repository.ReservationRepository;
import com.rental.room.repository.RoomRepository;
import com.airbnb.events.ReservationCancelledEvent;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import javax.management.InstanceNotFoundException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final RefundServiceClient refundServiceClient;
    private final RecommendationServiceClient recommendationServiceClient;
    private final ReservationEventsProducer producer;

    public ReservationService(ReservationRepository reservationRepository,
                              RoomRepository roomRepository,
                              RefundServiceClient refundServiceClient,
                              RecommendationServiceClient recommendationServiceClient,
                              ReservationEventsProducer producer) {
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
        this.refundServiceClient = refundServiceClient;
        this.recommendationServiceClient = recommendationServiceClient;
        this.producer = producer;
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Optional<Reservation> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    public List<Reservation> getReservationsByRoomId(Long roomId) {
        return reservationRepository.findByRoomId(roomId);
    }

    public boolean isRoomAvailable(Long roomId, LocalDate checkInDate, LocalDate checkOutDate) {
        if (checkInDate == null || checkOutDate == null) {
            throw new IllegalArgumentException("Daty zameldowania i wymeldowania są wymagane");
        }
        
        if (checkInDate.isAfter(checkOutDate) || checkInDate.isEqual(checkOutDate)) {
            throw new IllegalArgumentException("Data zameldowania musi być wcześniejsza niż data wymeldowania");
        }

        boolean roomExists = roomRepository.existsById(roomId);
        if (!roomExists) {
            throw new IllegalArgumentException("Pokój o ID " + roomId + " nie istnieje");
        }

        List<Reservation> conflictingReservations = reservationRepository
                .findActiveReservationsForRoomInPeriod(roomId, checkInDate, checkOutDate);

        return conflictingReservations.isEmpty();
    }

    @Transactional
    public Reservation createReservation(Long roomId, Reservation reservation) throws Exception {
        try {
            LocalDate checkIn = reservation.getCheckInDate();
            LocalDate checkOut = reservation.getCheckOutDate();
            
            boolean isAvailable = isRoomAvailable(roomId, checkIn, checkOut);
            if (!isAvailable) {
                throw new Exception("Pokój jest już zarezerwowany w podanym terminie");
            }
            
            Optional<Room> roomOptional = roomRepository.findById(roomId);
            Room room = roomOptional.get();
            
            reservation.setRoom(room);
            reservation.calculateTotalPrice();
            
            Reservation savedReservation = reservationRepository.save(reservation);

            // Send ReservationCreatedEvent to Kafka
            ReservationCreatedEvent event = new ReservationCreatedEvent(
                savedReservation.getId(),
                room.getId(),
                savedReservation.getGuestName(),
                savedReservation.getGuestEmail(),
                savedReservation.getCheckInDate(),
                savedReservation.getCheckOutDate(),
                savedReservation.getTotalPrice()
            );
            producer.sendReservationCreatedEvent(event);

            return savedReservation;
            
        } catch (IllegalArgumentException e) {
            throw new Exception(e.getMessage());
        }
    }

    @Transactional
    public void cancelReservation(Long id, CancellerType cancellerType) throws InstanceNotFoundException {
        Reservation reservation = this.getReservationById(id).orElseThrow(InstanceNotFoundException::new);
        if (cancellerType == CancellerType.GUEST) {
            // TODO: check if can be cancelled
            reservation.setStatus(ReservationStatus.CANCELLED_BY_GUEST.toString());
            refundServiceClient.refundGuest(id);
        } else if (cancellerType == CancellerType.OWNER) {
            reservation.setStatus(ReservationStatus.CANCELLED_BY_OWNER.toString());
            refundServiceClient.refundGuest(id);
            if(daysUntilStay(reservation.getCheckInDate()) <= 30) {
                refundServiceClient.refundAdditionalCompensation(id);
            }

            recommendationServiceClient.sendRecommendationEmail(reservation.getGuestEmail());
        }
        ReservationCancelledEvent event = new ReservationCancelledEvent(id, cancellerType.toString());
        // producer.sendCancellationEvent(event); póki co nikt nie nasłuchuje, więc nie ma sensu tego wysyłać
    }

    private long daysUntilStay(LocalDate checkInDate) {
        return ChronoUnit.DAYS.between(LocalDate.now(), checkInDate);
    }
}