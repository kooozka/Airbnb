package com.rental.rating_service.ratings.service;

import com.rental.rating_service.ratings.dto.RatingAddDTO;
import com.rental.rating_service.ratings.mapper.RatingMapper;
import com.rental.rating_service.ratings.model.Rating;
import com.rental.rating_service.ratings.repository.RatingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RatingService {
    private final RatingRepository ratingRepository;
    private final RatingMapper ratingMapper;

    public RatingService(RatingRepository ratingRepository, RatingMapper ratingMapper) {
        this.ratingRepository=ratingRepository;
        this.ratingMapper=ratingMapper;
    }

    public Rating createRating(RatingAddDTO ratingDTO){
        Rating rating = ratingMapper.toEntity(ratingDTO);
        rating.setCreatedAt(LocalDateTime.now());
        return ratingRepository.save(rating);
    }

    public List<Rating> getRatingsForProperty(Long listingId){
        return ratingRepository.findByListingId(listingId);
    }

    public void deleteRating(Long ratingId){
        ratingRepository.deleteById(ratingId);
    }
}
