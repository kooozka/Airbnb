package com.payment.paymentservice.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.paymentservice.model.Payment;
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

    public PaymentController(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPayuPayment(@RequestParam double total, @RequestParam String currency) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String body = "grant_type=client_credentials&client_id=" + clientId + "&client_secret=" + clientSecret;
        HttpEntity<String> request = new HttpEntity<>(body, headers);
        Map<String, String> tokenResponse = restTemplate.postForObject(oauthUrl, request, Map.class);
        String accessToken = tokenResponse != null ? tokenResponse.get("access_token") : null;
        if (accessToken == null) {
            return ResponseEntity.status(500).body("Błąd podczas uzyskiwania tokenu PayU");
        }

        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        Map<String, Object> payment = new HashMap<>();
        payment.put("customerIp", "127.0.0.1");
        payment.put("merchantPosId", posId);
        payment.put("description", "Test PayU payment");
        payment.put("currencyCode", currency);
        payment.put("totalAmount", String.valueOf((int)(total * 100)));
        payment.put("products", java.util.List.of(
                Map.of(
                        "name", "Test product",
                        "unitPrice", String.valueOf((int)(total * 100)),
                        "quantity", 1
                )
        ));
        HttpEntity<Map<String, Object>> paymentRequest = new HttpEntity<>(payment, headers);
        Map<String, Object> paymentResponse = restTemplate.postForObject(orderUrl, paymentRequest, Map.class);
        if (paymentResponse != null && paymentResponse.containsKey("redirectUri") && paymentResponse.containsKey("orderId")) {
            // Zapisz zamówienie w bazie
            String payuOrderId = (String) paymentResponse.get("orderId");
            Payment newOrder = Payment.builder()
                    .payuOrderId(payuOrderId)
                    .status("PENDING")
                    .amount(total)
                    .currency(currency)
                    .build();
            paymentRepository.save(newOrder);
            return ResponseEntity.ok(Map.of(
                "redirectUri", paymentResponse.get("redirectUri"),
                "orderId", payuOrderId
            ));
        } else {
            return ResponseEntity.status(500).body("Błąd podczas tworzenia płatności PayU");
        }
    }

    @GetMapping("/status")
    public ResponseEntity<?> getPaymentStatus(@RequestParam String paymentId) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            String body = "grant_type=client_credentials&client_id=" + clientId + "&client_secret=" + clientSecret;
            HttpEntity<String> request = new HttpEntity<>(body, headers);
            Map<String, String> tokenResponse = restTemplate.postForObject(oauthUrl, request, Map.class);
            String accessToken = tokenResponse != null ? tokenResponse.get("access_token") : null;
            if (accessToken == null) {
                return ResponseEntity.status(500).body("Błąd podczas uzyskiwania tokenu PayU");
            }

            headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            HttpEntity<Void> statusRequest = new HttpEntity<>(headers);
            String statusUrl = orderUrl + "/" + paymentId;
            ResponseEntity<String> response = restTemplate.exchange(statusUrl, HttpMethod.GET, statusRequest, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            JsonNode payments = root.path("orders");
            String status = null;
            if (payments.isArray() && payments.size() > 0) {
                status = payments.get(0).path("status").asText();
            }
            if (status != null) {
                Optional<Payment> paymentOpt = paymentRepository.findByPayuOrderId(paymentId);
                if (paymentOpt.isPresent()) {
                    Payment payment = paymentOpt.get();
                    payment.setStatus(status);
                    paymentRepository.save(payment);
                }
            }
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            logger.error("Błąd podczas pobierania statusu zamówienia: {}", e.getMessage());
            return ResponseEntity.status(500).body("Błąd podczas pobierania statusu zamówienia: " + e.getMessage());
        }
    }

    @GetMapping("/order")
    public ResponseEntity<?> getLocalPayment(@RequestParam String orderId) {
        Optional<Payment> orderOpt = paymentRepository.findByPayuOrderId(orderId);
        if (orderOpt.isPresent()) {
            return ResponseEntity.ok(orderOpt.get());
        } else {
            return ResponseEntity.status(404).body("Nie znaleziono zamówienia o podanym orderId");
        }
    }
}
