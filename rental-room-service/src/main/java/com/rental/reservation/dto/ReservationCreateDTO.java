package com.rental.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationCreateDTO {
    
    private String guestName;
    private String guestEmail;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
}