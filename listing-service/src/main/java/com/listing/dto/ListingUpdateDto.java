package com.listing.dto;

import com.listing.model.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListingUpdateDto {
  private Long id;
  private String title;
  private String description;
  private Double pricePerNight;
  private int nrOfRooms;
  private int maxGuests;
  private Address address;
  // private UUID ownerId;
}
