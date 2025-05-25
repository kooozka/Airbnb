package com.notification.listener;

import com.airbnb.events.UserNotificationEvent;
import com.notification.service.EmailService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {

  private final EmailService emailService;

  public NotificationListener(EmailService emailService) {
    this.emailService = emailService;
  }

  @KafkaListener(topics = "user-notifications", groupId = "notification-group")
  public void listen(UserNotificationEvent event) {
    emailService.sendEmail(event.getEmail(), event.getSubject(), event.getContent());
  }
}
