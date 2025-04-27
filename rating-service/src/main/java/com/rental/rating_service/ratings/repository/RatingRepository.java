package com.rental.rating_service.ratings.repository;

import com.rental.rating_service.ratings.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByListingId(Long listingId);
}
