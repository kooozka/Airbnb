package com.rental.rating_service.listing.service;

import com.rental.rating_service.listing.model.Listing;
import com.rental.rating_service.listing.repository.ListingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ListingService {
    private final ListingRepository listingRepository;

    public ListingService(ListingRepository listingRepository) {
        this.listingRepository=listingRepository;
    }

    public List<Listing> getAllListings(){
        return listingRepository.findAll();
    }

    public Optional<Listing> findListingById(Long listingId){
        return listingRepository.findById(listingId);
    }
}
