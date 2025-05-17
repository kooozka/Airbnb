package com.rental.rating_service.listing.listener;

import com.airbnb.events.ListingCreatedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rental.rating_service.listing.model.Listing;
import com.rental.rating_service.listing.repository.ListingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ListingCreatedListener {

    private final ListingRepository listingRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "listing-created", groupId = "rating-group")
    public void handleListingCreatedEvent(ListingCreatedEvent event){
        Listing listing = Listing.builder()
                .id(event.getId())
                .title(event.getTitle())
                .build();

        listingRepository.save(listing);
        System.out.println("Received new listing: " + listing.getTitle());
    }
}
