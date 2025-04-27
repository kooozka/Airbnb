package com.rental.rating_service.ratings.controller;

import com.rental.rating_service.ratings.dto.RatingAddDTO;
import com.rental.rating_service.ratings.model.Rating;
import com.rental.rating_service.ratings.service.RatingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService=ratingService;
    }

    @PostMapping("/add")
    public ResponseEntity<Rating> addRating(@RequestBody RatingAddDTO rating) {
        return ResponseEntity.ok(ratingService.createRating(rating));
    }

    @GetMapping("/listing/{listingId}")
    public ResponseEntity<List<Rating>> getRatings(@PathVariable Long listingId) {
        return ResponseEntity.ok(ratingService.getRatingsForProperty(listingId));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteRating(@PathVariable Long id) {
        ratingService.deleteRating(id);
        return ResponseEntity.noContent().build();
    }
}
