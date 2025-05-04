package com.listing.producer;

import com.listing.model.Listing;
import com.airbnb.events.ListingCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListingProducer {

  private final KafkaTemplate<String, ListingCreatedEvent> listingCreatedEventKafkaTemplate;
  public void sendListingCreatedEvent(Listing listing) {
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

    listingCreatedEventKafkaTemplate.send("listing-created", listing.getId().toString(), event);

  }
}
