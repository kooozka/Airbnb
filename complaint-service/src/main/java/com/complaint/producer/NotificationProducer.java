package com.complaint.producer;

import com.airbnb.events.UserNotificationEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationProducer {

  private final KafkaTemplate<String, UserNotificationEvent> kafkaTemplate;

  public NotificationProducer(KafkaTemplate<String, UserNotificationEvent> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void sendNotification(UserNotificationEvent event) {
    kafkaTemplate.send("user-notifications", event);
  }
}