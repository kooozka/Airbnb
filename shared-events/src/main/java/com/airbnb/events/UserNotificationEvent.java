package com.airbnb.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserNotificationEvent {
  private String email;
  private String subject;
  private String content;
}
