package com.listing.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.listing.dto.event.ListingCreatedEvent;
import com.listing.model.Listing;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListingProducer {

  private final KafkaTemplate<String, String> kafkaTemplate;

  private final ObjectMapper objectMapper;

  public void sendListingCreatedEvent(Listing listing) {
    try {
      ListingCreatedEvent event = new ListingCreatedEvent(
          listing.getId(),
          listing.getTitle(),
          listing.getDescription(),
          listing.getPricePerNight(),
          listing.getNrOfRooms(),
          listing.getMaxGuests(),
          listing.getAddress().getStreet() + ' ' +
          listing.getAddress().getCity() + ' ' +
          listing.getAddress().getCountry() + ' ' +
          listing.getAddress().getPostalCode() + ' ' +
          listing.getAddress().getHouseNumber() + ' '
      );

      String payload = objectMapper.writeValueAsString(event);
      kafkaTemplate.send("listing-created", listing.getId().toString(), payload);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to serialize ListingCreatedEvent", e);
    }
  }
}
