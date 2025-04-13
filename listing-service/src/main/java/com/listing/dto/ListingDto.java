package com.listing.dto;

import com.listing.model.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListingDto {
  private String title;
  private String description;
  private Double pricePerNight;
  private int nrOfRooms;
  private int maxGuests;
  private Address address;
}
