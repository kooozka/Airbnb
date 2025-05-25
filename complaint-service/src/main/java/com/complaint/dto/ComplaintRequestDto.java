package com.complaint.dto;

import lombok.Data;

@Data
public class ComplaintRequestDto {
  private Long userId;
  private String description;
  private String email;
  private String secondPartyEmail;
}
