package com.listing.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListingCreatedEvent {
  private Long id;
  private String title;
  private String description;
  private Double pricePerNight;
  private int nrOfRooms;
  private int maxGuests;

  private String location;
}
