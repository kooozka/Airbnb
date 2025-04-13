package com.rental.room.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rental.room.dto.event.ListingCreatedEvent;
import com.rental.room.model.Room;
import com.rental.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ListingCreatedListener {

  private final RoomRepository roomRepository;
  private final ObjectMapper objectMapper;

  @KafkaListener(topics = "listing-created", groupId = "rental-room-group")
  public void listen(String message) {
    try {
      ListingCreatedEvent event = objectMapper.readValue(message, ListingCreatedEvent.class);

      Room room = new Room();

      room.setTitle(event.getTitle());
      room.setDescription(event.getDescription());
      room.setMaxGuests(event.getMaxGuests());
      room.setNrOfRooms(event.getNrOfRooms());
      room.setPricePerNight(event.getPricePerNight());
      room.setLocation(event.getLocation());

      roomRepository.save(room);
      System.out.println("Received and saved new listing: " + room.getTitle());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}