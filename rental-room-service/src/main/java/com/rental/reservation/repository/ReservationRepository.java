package com.rental.reservation.repository;

import com.rental.reservation.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByRoomId(Long roomId);

    @Query("SELECT r FROM Reservation r WHERE r.room.id = :roomId " +
           "AND ((r.checkInDate < :checkOutDate AND r.checkOutDate > :checkInDate))")
    List<Reservation> findActiveReservationsForRoomInPeriod(
            @Param("roomId") Long roomId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate);

}