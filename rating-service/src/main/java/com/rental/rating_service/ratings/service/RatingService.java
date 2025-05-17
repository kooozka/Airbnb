package com.rental.rating_service.ratings.service;

import com.rental.rating_service.listing.service.ListingService;
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
    private final ListingService listingService;

    public RatingService(RatingRepository ratingRepository, RatingMapper ratingMapper, ListingService listingService) {
        this.ratingRepository=ratingRepository;
        this.ratingMapper=ratingMapper;
        this.listingService=listingService;
    }

    public Rating createRating(RatingAddDTO ratingDTO){
        if(checkIfListingExists(ratingDTO.getListingId())){
            Rating rating = ratingMapper.toEntity(ratingDTO);
            rating.setCreatedAt(LocalDateTime.now());
            return ratingRepository.save(rating);
        }
        else
            return Rating.builder().build();
    }

    private boolean checkIfListingExists(Long listingId){
        return listingService.findListingById(listingId).isPresent();
    }

    public List<Rating> getRatingsForProperty(Long listingId){
        return ratingRepository.findByListingId(listingId);
    }

    public void deleteRating(Long ratingId){
        ratingRepository.deleteById(ratingId);
    }
}
