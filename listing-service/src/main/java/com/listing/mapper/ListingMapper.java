package com.listing.mapper;

import com.listing.dto.ListingCreateDto;
import com.listing.dto.ListingDto;
import com.listing.dto.ListingUpdateDto;
import com.listing.model.Listing;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ListingMapper {

  @Mapping(source = "title", target = "title")
  @Mapping(source = "description", target = "description")
  @Mapping(source = "pricePerNight", target = "pricePerNight")
  @Mapping(source = "nrOfRooms", target = "nrOfRooms")
  @Mapping(source = "maxGuests", target = "maxGuests")
  @Mapping(source = "address", target = "address")
  Listing toEntity(ListingCreateDto dto);

  ListingDto toDto(Listing listing);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntityFromDto(ListingUpdateDto dto, @MappingTarget Listing listing);

}
