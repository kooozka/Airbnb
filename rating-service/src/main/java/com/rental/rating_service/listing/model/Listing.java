package com.rental.rating_service.listing.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Listing {
    @Id
    private Long id;
    private String title;

    //private Long ownerId;
}
