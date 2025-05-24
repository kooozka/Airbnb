package com.payment.paymentservice.listener;

import com.airbnb.events.ReservationCancelledEvent;
import com.airbnb.events.ReservationCreatedEvent;
import com.payment.paymentservice.controller.PaymentController;
import com.payment.paymentservice.model.Payment;
import com.payment.paymentservice.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class ReservationCancelledListener {

    private static final Logger logger = LoggerFactory.getLogger(ReservationCreatedListener.class);
    private final PaymentRepository paymentRepository;
    private final PaymentController paymentController;

    @Value("${payu.client_id}")
    private String clientId;
    @Value("${payu.client_secret}")
    private String clientSecret;
    @Value("${payu.pos_id}")
    private String posId;
    @Value("${payu.oauth_url}")
    private String oauthUrl;
    @Value("${payu.order_url}")
    private String orderUrl;

    public ReservationCancelledListener(PaymentRepository paymentRepository, PaymentController paymentController) {
        this.paymentRepository = paymentRepository;
        this.paymentController = paymentController;
    }

    @KafkaListener(topics = "reservation-cancelled", groupId = "payment-group")
    public void handleReservationCancelled(ReservationCancelledEvent event) {
        logger.info("Otrzymano event ReservationCancelled: {}", event);

        try {
        if (event.getPayuOrderId() == null || event.getPayuOrderId().isEmpty()) {
            logger.error("Brak PayU orderId w evencie anulowania rezerwacji");
            return;
        }

        ResponseEntity<?> response = paymentController.refundPayment(
                event.getPayuOrderId(),
                event.getAmount()
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            if (response.getBody() instanceof Map) {
                Map<String, Object> body = (Map<String, Object>) response.getBody();
                logger.info("Automatyczny zwrot został przetworzony, refundId: {}", body.get("refundId"));
            } else {
                logger.info("Automatyczny zwrot został przetworzony: {}", response.getBody());
            }
        } else {
            logger.error("Błąd podczas przetwarzania automatycznego zwrotu: {}", response.getBody());
        }

        } catch (Exception e) {
            logger.error("Błąd podczas przetwarzania eventu anulowania rezerwacji: {}", e.getMessage(), e);
        }
    }
}
