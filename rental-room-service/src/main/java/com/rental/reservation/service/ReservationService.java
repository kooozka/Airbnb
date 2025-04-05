package com.rental.reservation.service;

import com.rental.reservation.model.Reservation;
import com.rental.room.model.Room;
import com.rental.reservation.repository.ReservationRepository;
import com.rental.room.repository.RoomRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;

    public ReservationService(ReservationRepository reservationRepository, RoomRepository roomRepository) {
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
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
            
            return reservationRepository.save(reservation);
            
        } catch (IllegalArgumentException e) {
            throw new Exception(e.getMessage());
        }
    }
}