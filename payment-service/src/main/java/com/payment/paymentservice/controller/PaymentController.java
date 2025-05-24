package com.payment.paymentservice.controller;

import com.airbnb.events.PaymentStatusEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.paymentservice.model.Payment;
import com.payment.paymentservice.producer.PaymentEventsProducer;
import com.payment.paymentservice.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

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

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    private final PaymentRepository paymentRepository;
    private final PaymentEventsProducer paymentEventsProducer;

    public PaymentController(PaymentRepository paymentRepository, PaymentEventsProducer paymentEventsProducer) {
        this.paymentRepository = paymentRepository;
        this.paymentEventsProducer = paymentEventsProducer;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPayuPayment(@RequestParam double total, @RequestParam String currency) {
        String token = getPayuAccessToken();
        if (token == null) {
            return ResponseEntity.status(500).body("Błąd podczas uzyskiwania tokenu PayU");
        }

        HttpHeaders headers = createAuthHeaders(token);
        Map<String, Object> payment = Map.of(
                "customerIp", "127.0.0.1",
                "merchantPosId", posId,
                "description", "Test PayU payment",
                "currencyCode", currency,
                "totalAmount", String.valueOf((int)(total * 100)),
                "products", List.of(Map.of(
                        "name", "Test product",
                        "unitPrice", String.valueOf((int)(total * 100)),
                        "quantity", 1
                ))
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payment, headers);
        Map<String, Object> response = new RestTemplate().postForObject(orderUrl, request, Map.class);

        if (response != null && response.containsKey("redirectUri") && response.containsKey("orderId")) {
            Payment newOrder = Payment.builder()
                    .payuOrderId((String) response.get("orderId"))
                    .status("PENDING")
                    .amount(total)
                    .currency(currency)
                    .build();
            paymentRepository.save(newOrder);
            return ResponseEntity.ok(Map.of(
                    "redirectUri", response.get("redirectUri"),
                    "orderId", response.get("orderId")
            ));
        }
        return ResponseEntity.status(500).body("Błąd podczas tworzenia płatności PayU");
    }

    @GetMapping("/status")
    public ResponseEntity<?> getPaymentStatus(@RequestParam String paymentId, @RequestParam long reservationId) {
        String token = getPayuAccessToken();
        if (token == null) return ResponseEntity.status(500).body("Błąd podczas uzyskiwania tokenu PayU");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        String url = orderUrl + "/" + paymentId;

        try {
            ResponseEntity<String> response = new RestTemplate().exchange(url, HttpMethod.GET, request, String.class);
            JsonNode statusNode = new ObjectMapper().readTree(response.getBody()).path("orders").get(0).path("status");

            String status = statusNode.asText(null);
            paymentRepository.findByPayuOrderId(paymentId).ifPresent(payment -> {
                payment.setStatus(status);
                paymentRepository.save(payment);
                paymentEventsProducer.sendPaymentStatusEvent(new PaymentStatusEvent(reservationId, status));
            });

            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            logger.error("Status error: {}", e.getMessage());
            return ResponseEntity.status(500).body("Błąd podczas pobierania statusu zamówienia: " + e.getMessage());
        }
    }

    @PostMapping("/refund")
    public ResponseEntity<?> refundPayment(@RequestParam String orderId, @RequestParam(required = false) Double amount) {
        Optional<Payment> paymentOpt = paymentRepository.findByPayuOrderId(orderId);
        if (paymentOpt.isEmpty()) return ResponseEntity.status(404).body("Nie znaleziono zamówienia");

        Payment payment = paymentOpt.get();
        double refundAmount = amount != null ? amount : payment.getAmount();

        String token = getPayuAccessToken();
        if (token == null) return ResponseEntity.status(500).body("Błąd tokenu PayU");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        Map<String, Object> refundData = Map.of(
                "orderId", orderId,
                "refund", new HashMap<>() {{
                    put("description", "Zwrot płatności");
                    if (amount != null && Math.abs(amount - payment.getAmount()) > 0.01) {
                        put("amount", String.valueOf((int) (refundAmount * 100)));
                    }
                }}
        );

        String url = orderUrl + "/" + orderId + "/refunds";
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(refundData, headers);

        try {
            ResponseEntity<Map> response = new RestTemplate().exchange(url, HttpMethod.POST, request, Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                payment.setStatus("REFUNDED");
                paymentRepository.save(payment);

                Object refundId = Optional.ofNullable(response.getBody())
                        .map(b -> b.get("refundId") != null ? b.get("refundId") :
                                Optional.ofNullable(b.get("refund"))
                                        .filter(Map.class::isInstance)
                                        .map(Map.class::cast)
                                        .map(r -> r.get("refundId"))
                                        .orElse("unknown")
                        ).orElse("unknown");

                return ResponseEntity.ok(Map.of(
                        "status", "success",
                        "refundId", refundId,
                        "fullResponse", response.getBody()
                ));
            }
            return ResponseEntity.status(response.getStatusCode()).body("Błąd przy zwrocie: " + response.getBody());
        } catch (Exception e) {
            logger.error("Refund error: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of(
                    "status", "error",
                    "message", "Błąd podczas zwrotu",
                    "details", e.getMessage()
            ));
        }
    }

    @GetMapping("/refund/status")
    public ResponseEntity<?> getRefundStatus(@RequestParam String orderId, @RequestParam String refundId) {
        Optional<Payment> paymentOpt = paymentRepository.findByPayuOrderId(orderId);
        if (paymentOpt.isEmpty()) return ResponseEntity.status(404).body("Nie znaleziono zamówienia");

        String token = getPayuAccessToken();
        if (token == null) return ResponseEntity.status(500).body("Błąd tokenu PayU");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        String url = orderUrl + "/" + orderId + "/refunds/" + refundId;

        try {
            ResponseEntity<Map> response = new RestTemplate().exchange(url, HttpMethod.GET, request, Map.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            logger.error("Refund status error: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of(
                    "status", "error",
                    "message", "Błąd podczas pobierania statusu zwrotu",
                    "details", e.getMessage()
            ));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllPayments() {
        try {
            Iterable<Payment> payments = paymentRepository.findAll();
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            logger.error("Błąd podczas pobierania płatności: {}", e.getMessage());
            return ResponseEntity.status(500).body("Błąd podczas pobierania płatności: " + e.getMessage());
        }
    }

    private String getPayuAccessToken() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String body = "grant_type=client_credentials&client_id=" + clientId + "&client_secret=" + clientSecret;
        HttpEntity<String> request = new HttpEntity<>(body, headers);
        Map<String, String> response = restTemplate.postForObject(oauthUrl, request, Map.class);
        return response != null ? response.get("access_token") : null;
    }

    private HttpHeaders createAuthHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        return headers;
    }
}
