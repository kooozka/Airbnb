package com.rental.rating_service.ratings.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingAddDTO {
    private Long listingId;
    private Long userId;
    private String reviewComment;
    private int rating;
}
