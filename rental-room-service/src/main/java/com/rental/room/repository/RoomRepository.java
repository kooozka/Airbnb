package com.rental.room.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.rental.room.model.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {
}