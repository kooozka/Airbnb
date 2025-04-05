package com.rental.reservation.controller;

import com.rental.reservation.dto.ReservationCreateDTO;
import com.rental.reservation.model.Reservation;
import com.rental.reservation.service.ReservationService;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public ResponseEntity<List<Reservation>> getAllReservations() {
        List<Reservation> reservations = reservationService.getAllReservations();
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable Long id) {
        Optional<Reservation> reservation = reservationService.getReservationById(id);
        return reservation.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<Reservation>> getReservationsByRoomId(@PathVariable Long roomId) {
        List<Reservation> reservations = reservationService.getReservationsByRoomId(roomId);
        return ResponseEntity.ok(reservations);
    }

    @PostMapping("/room/{roomId}")
    public ResponseEntity<?> createReservation(
            @PathVariable Long roomId,
            @RequestBody ReservationCreateDTO reservationDTO) {
        try {
            Reservation reservation = Reservation.builder()
                .guestName(reservationDTO.getGuestName())
                .guestEmail(reservationDTO.getGuestEmail())
                .checkInDate(reservationDTO.getCheckInDate())
                .checkOutDate(reservationDTO.getCheckOutDate())
                .build();
            
            Reservation createdReservation = reservationService.createReservation(roomId, reservation);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdReservation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/room/{roomId}/availability")
    public ResponseEntity<?> checkRoomAvailability(
            @PathVariable Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut) {
        
        boolean isAvailable = reservationService.isRoomAvailable(roomId, checkIn, checkOut);
        
        return ResponseEntity.ok().body(
            new AvailabilityResponse(roomId, checkIn, checkOut, isAvailable)
        );
    }

    @Getter
    private static class AvailabilityResponse {
        private final Long roomId;
        private final LocalDate checkIn;
        private final LocalDate checkOut;
        private final boolean available;

        public AvailabilityResponse(Long roomId, LocalDate checkIn, LocalDate checkOut, boolean available) {
            this.roomId = roomId;
            this.checkIn = checkIn;
            this.checkOut = checkOut;
            this.available = available;
        }

    }
}