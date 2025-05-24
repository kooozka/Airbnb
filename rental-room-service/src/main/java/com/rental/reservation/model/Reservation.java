package com.rental.reservation.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.rental.room.model.Room;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "reservations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    @JsonBackReference
    private Room room;
    
    private String guestName;
    private String guestEmail;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String status; // to docelowo zamienimy na enum (PENDING, CONFIRMED, CANCELED, COMPLETED)
    private Double totalPrice;
    private String payuOrderId;
    
    public void calculateTotalPrice() {
        if (room != null && checkInDate != null && checkOutDate != null) {
            long daysOfStay = checkOutDate.toEpochDay() - checkInDate.toEpochDay();
            this.totalPrice = room.getPricePerNight() * daysOfStay;
        }
    }
}