package com.complaint.controller;

import com.complaint.dto.ComplaintDecisionDto;
import com.complaint.dto.ComplaintRequestDto;
import com.complaint.model.Complaint;
import com.complaint.service.ComplaintService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/complaints")
@RequiredArgsConstructor
public class ComplaintController {

  private final ComplaintService service;

  @PostMapping
  public ResponseEntity<Complaint> submitComplaint(@RequestBody ComplaintRequestDto dto) {
    Complaint created = service.createComplaint(dto);
    return ResponseEntity.ok(created);
  }

  @GetMapping
  public ResponseEntity<List<Complaint>> getComplaints() {
    List<Complaint> complaints = service.getAllComplaints();
    return ResponseEntity.ok(complaints);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Complaint> getComplaintById(@PathVariable Long id) {
    Complaint complaint = service.getComplaintById(id);
    return ResponseEntity.ok(complaint);
  }

  @PostMapping("/{id}/request-explanation")
  public ResponseEntity<Void> requestExplanation(@PathVariable Long id) {
    service.requestExplanation(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{id}/explanation")
  public ResponseEntity<Void> submitExplanation(@PathVariable Long id, @RequestBody String explanation) {
    service.processExplanation(id, explanation);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{id}/resolve")
  public ResponseEntity<Void> resolve(@PathVariable Long id, @RequestBody ComplaintDecisionDto dto) {
    service.resolveComplaint(id, dto);
    return ResponseEntity.noContent().build();
  }
}
