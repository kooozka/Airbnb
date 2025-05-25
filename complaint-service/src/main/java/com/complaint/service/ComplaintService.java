package com.complaint.service;

import com.airbnb.events.UserNotificationEvent;
import com.complaint.dto.ComplaintDecisionDto;
import com.complaint.dto.ComplaintRequestDto;
import com.complaint.model.Complaint;
import com.complaint.producer.NotificationProducer;
import com.complaint.repository.ComplaintRepository;
import lombok.RequiredArgsConstructor;
import com.complaint.model.ComplaintStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ComplaintService {
  private final ComplaintRepository repository;
  private final NotificationProducer notificationProducer;

  public Complaint createComplaint(ComplaintRequestDto dto) {
    Complaint complaint = Complaint.builder()
        .userId(dto.getUserId())
        .description(dto.getDescription())
        .status(ComplaintStatus.SUBMITTED)
        .email(dto.getEmail())
        .secondPartyEmail(dto.getSecondPartyEmail())
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
    return repository.save(complaint);
  }

  public List<Complaint> getAllComplaints() {
    return repository.findAll();
  }

  public Complaint getComplaintById(Long id) {
    return repository.findById(id).orElseThrow(() -> new RuntimeException("Complaint with id " + id + " not found"));
  }

  public void requestExplanation(Long complaintId) {
    Complaint complaint = repository.findById(complaintId)
        .orElseThrow(() -> new RuntimeException("Complaint not found"));

    complaint.setStatus(ComplaintStatus.WAITING_FOR_EXPLANATION);

    UserNotificationEvent event = new UserNotificationEvent(
        complaint.getSecondPartyEmail(),
        "Prośba o wyjaśnienie w związku ze skargą",
        "Prosimy o udzielenie wyjaśnień dotyczących skargi: " + complaint.getDescription() + " od użytkownika " + complaint.getEmail()
    );

    notificationProducer.sendNotification(event);
    System.out.println("Email requesting explanation sent to " + complaint.getSecondPartyEmail());
  }

  public void processExplanation(Long complaintId, String explanation) {
    Complaint complaint = repository.findById(complaintId).orElseThrow(() -> new RuntimeException("Complaint not found"));
    complaint.setSecondPartyExplanation(explanation);
    complaint.setStatus(ComplaintStatus.EXPLANATION_RECEIVED);
    complaint.setUpdatedAt(LocalDateTime.now());

    repository.save(complaint);
  }

  public void resolveComplaint(Long complaintId, ComplaintDecisionDto decision) {
    Complaint complaint = repository.findById(complaintId).orElseThrow(() -> new RuntimeException("Complaint not found"));
    complaint.setStatus(decision.isAccepted() ? ComplaintStatus.RESOLVED_ACCEPTED : ComplaintStatus.RESOLVED_REJECTED);
    complaint.setUpdatedAt(LocalDateTime.now());
    repository.save(complaint);

    UserNotificationEvent event = new UserNotificationEvent(
        complaint.getEmail(),
        "Decyzja w związku ze skargą",
        decision.getExplanation()
    );
    UserNotificationEvent secondPartyEvent = new UserNotificationEvent(
        complaint.getSecondPartyEmail(),
        "Decyzja w związku ze skargą",
        decision.getExplanation()
    );

    notificationProducer.sendNotification(event);
    notificationProducer.sendNotification(secondPartyEvent);
    System.out.println("Emails with decision sent to " + complaint.getEmail() + " and " + complaint.getSecondPartyEmail());
  }

}
