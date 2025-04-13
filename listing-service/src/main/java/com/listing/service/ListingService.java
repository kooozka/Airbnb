package com.listing.service;

import com.listing.dto.ListingCreateDto;
import com.listing.dto.ListingDto;
import com.listing.dto.ListingUpdateDto;
import com.listing.mapper.ListingMapper;
import com.listing.model.Listing;
import com.listing.producer.ListingProducer;
import com.listing.repository.ListingRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ListingService {
  private final ListingRepository listingRepository;
  private final ListingMapper listingMapper;
  private final ListingProducer listingProducer;

  public ListingService(ListingRepository listingRepository, ListingMapper listingMapper, ListingProducer listingProducer) {
    this.listingRepository = listingRepository;
    this.listingMapper = listingMapper;
    this.listingProducer = listingProducer;
  }

  public Long createListing(ListingCreateDto dto) {
    Listing listing = listingMapper.toEntity(dto);
    listing = listingRepository.save(listing);
    listingProducer.sendListingCreatedEvent(listing);
    return listing.getId();
  }

  public ListingDto getListing(Long id) {
    Listing listing = listingRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Listing not found"));
    return listingMapper.toDto(listing);
  }

  public void updateListing(Long id, ListingUpdateDto dto) {
    Listing existing = listingRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Listing not found"));
    listingMapper.updateEntityFromDto(dto, existing);
    listingRepository.save(existing);
  }

  public void deleteListing(Long id) {
    listingRepository.deleteById(id);
  }

//  public List<ListingDto> getListingsByOwnerId(UUID ownerId) {
//    return listingRepository.findAllByOwnerId(ownerId).stream()
//        .map(listingMapper::toDto)
//        .collect(Collectors.toList());
//  }

}
