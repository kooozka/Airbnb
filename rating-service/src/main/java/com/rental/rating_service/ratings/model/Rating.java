package com.rental.rating_service.ratings.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="ratings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rating {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private Long listingId;
    private Long userId;

    private String reviewComment;

    private int rating;

    private LocalDateTime createdAt;
}
