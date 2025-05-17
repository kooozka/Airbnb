package com.rental.rating_service.listing.repository;

import com.rental.rating_service.listing.model.Listing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListingRepository extends JpaRepository<Listing, Long> {
}
