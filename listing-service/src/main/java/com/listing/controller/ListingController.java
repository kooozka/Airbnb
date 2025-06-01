package com.listing.controller;

import com.listing.dto.ListingCreateDto;
import com.listing.dto.ListingDto;
import com.listing.dto.ListingUpdateDto;
import com.listing.service.ListingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequestMapping("/api/listings")
@RestController
public class ListingController {

  private final ListingService listingService;

  public ListingController(ListingService listingService) {
    this.listingService = listingService;
  }

  @PostMapping
  public ResponseEntity<?> createListing(@RequestBody ListingCreateDto dto) {
    Long id = listingService.createListing(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
            "id", id,
            "status", "created",
            "message", "Listing utworzony pomyślnie"
    ));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ListingDto> getListing(@PathVariable Long id) {
    return ResponseEntity.ok(listingService.getListing(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<Long> updateListing(@PathVariable Long id, @RequestBody ListingUpdateDto dto) {
    listingService.updateListing(id, dto);
    return ResponseEntity.ok(id);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Long> deleteListing(@PathVariable Long id) {
    listingService.deleteListing(id);
    return ResponseEntity.ok(id);
  }

//  @GetMapping("/owner/{ownerId}")
//  public ResponseEntity<List<ListingDto>> getListingsByOwner(@RequestParam UUID ownerId) {
//    return ResponseEntity.ok(listingService.getListingsByOwnerId(ownerId));
//  }
}
