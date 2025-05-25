package com.complaint.dto;

import lombok.Data;

@Data
public class ComplaintDecisionDto {
  private boolean accepted;
  private String explanation;
}
