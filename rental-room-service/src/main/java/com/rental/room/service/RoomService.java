package com.rental.room.service;

import com.rental.room.model.Room;
import com.rental.room.repository.RoomRepository;
import com.rental.reservation.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;

    @Autowired
    public RoomService(RoomRepository roomRepository, ReservationRepository reservationRepository) {
        this.roomRepository = roomRepository;
        this.reservationRepository = reservationRepository;
    }

    public List<Room> findAllRooms() {
        return roomRepository.findAll();
    }

    public List<Room> findAvailableRooms(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null || checkIn.isAfter(checkOut)) {
            throw new IllegalArgumentException("Nieprawidłowe daty: data zameldowania musi być wcześniejsza niż data wymeldowania");
        }
        
        List<Room> allRooms = roomRepository.findAll();
        List<Room> availableRooms = new ArrayList<>();
        
        for (Room room : allRooms) {
            boolean isAvailable = reservationRepository
                .findActiveReservationsForRoomInPeriod(room.getId(), checkIn, checkOut)
                .isEmpty();
                
            if (isAvailable) {
                availableRooms.add(room);
            }
        }
        
        return availableRooms;
    }
}