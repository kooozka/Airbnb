package com.rental.room.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;
import java.util.List;
import com.rental.room.model.Room;
import com.rental.room.service.RoomService;

@RestController
@RequestMapping("/api")
public class RoomController {
    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping("/rooms")
    public List<Room> getAllRooms() {
        return roomService.findAllRooms();
    }
    
    @GetMapping("/rooms/available")
    public ResponseEntity<?> getAvailableRooms(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut) {
        
        try {
            List<Room> availableRooms = roomService.findAvailableRooms(checkIn, checkOut);
            return ResponseEntity.ok(availableRooms);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}