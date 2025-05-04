package com.payment.paymentservice.listener;

import com.airbnb.events.ReservationCreatedEvent;
import com.payment.paymentservice.model.Payment;
import com.payment.paymentservice.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Service
public class ReservationCreatedListener {
    private static final Logger logger = LoggerFactory.getLogger(ReservationCreatedListener.class);
    private final PaymentRepository paymentRepository;

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

    public ReservationCreatedListener(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @KafkaListener(topics = "reservation-created", groupId = "payment-group")
    public void handleReservationCreated(ReservationCreatedEvent event) {
        logger.info("Otrzymano event ReservationCreated: {}", event);
        createPayuPaymentForEvent(event);
    }

    private void createPayuPaymentForEvent(ReservationCreatedEvent event) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            String body = "grant_type=client_credentials&client_id=" + clientId + "&client_secret=" + clientSecret;
            HttpEntity<String> request = new HttpEntity<>(body, headers);
            Map<String, String> tokenResponse = restTemplate.postForObject(oauthUrl, request, Map.class);
            String accessToken = tokenResponse != null ? tokenResponse.get("access_token") : null;
            if (accessToken == null) {
                logger.error("Błąd podczas uzyskiwania tokenu PayU");
                return;
            }

            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);
            Map<String, Object> payment = new HashMap<>();
            payment.put("customerIp", "127.0.0.1");
            payment.put("merchantPosId", posId);
            payment.put("description", "Rezerwacja nr " + event.getReservationId());
            payment.put("currencyCode", "PLN");
            payment.put("totalAmount", String.valueOf((int)(event.getTotalPrice() * 100)));
            payment.put("products", java.util.List.of(
                    Map.of(
                            "name", "Rezerwacja pokoju",
                            "unitPrice", String.valueOf((int)(event.getTotalPrice() * 100)),
                            "quantity", 1
                    )
            ));
            HttpEntity<Map<String, Object>> paymentRequest = new HttpEntity<>(payment, headers);
            Map<String, Object> paymentResponse = restTemplate.postForObject(orderUrl, paymentRequest, Map.class);
            if (paymentResponse != null && paymentResponse.containsKey("redirectUri") && paymentResponse.containsKey("orderId")) {
                String payuOrderId = (String) paymentResponse.get("orderId");
                Payment newOrder = Payment.builder()
                        .payuOrderId(payuOrderId)
                        .status("PENDING")
                        .amount(event.getTotalPrice())
                        .currency("PLN")
                        .build();
                paymentRepository.save(newOrder);
                logger.info("Wygenerowano link do płatności PayU: {}", paymentResponse.get("redirectUri"));
            } else {
                logger.error("Błąd podczas tworzenia płatności PayU");
            }
        } catch (Exception e) {
            logger.error("Błąd podczas rejestracji płatności: {}", e.getMessage());
        }
    }
}
