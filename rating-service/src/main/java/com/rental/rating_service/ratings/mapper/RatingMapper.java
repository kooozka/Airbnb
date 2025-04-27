package com.rental.rating_service.ratings.mapper;

import com.rental.rating_service.ratings.dto.RatingAddDTO;
import com.rental.rating_service.ratings.model.Rating;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel="spring")
public interface RatingMapper {

    @Mapping(source="listingId", target="listingId")
    @Mapping(source="userId", target="userId")
    @Mapping(source="reviewComment", target="reviewComment")
    @Mapping(source="rating", target="rating")
    Rating toEntity(RatingAddDTO dto);
}
