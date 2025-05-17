package com.rental.rating_service.listing.controller;

import com.rental.rating_service.listing.model.Listing;
import com.rental.rating_service.listing.service.ListingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ratings/listings")
public class ListingController {

    private final ListingService listingService;

    public ListingController(ListingService listingService) {
        this.listingService=listingService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Listing>> getListings(){
        return ResponseEntity.ok(listingService.getAllListings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Listing>> getListingById(@PathVariable Long id){
        return ResponseEntity.ok(listingService.findListingById(id));
    }
}
