package com.listing.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Embeddable
public class Address {
  private String street;
  private String city;
  private String country;
  private String postalCode;
  private String houseNumber;
}
