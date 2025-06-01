package com.airbnb.integration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Testcontainers
public class RoomReservationIntegrationTest {

    private static final Network NETWORK = Network.newNetwork();
    
    @Container
    private static final KafkaContainer kafka = new KafkaContainer()
            .withNetwork(NETWORK);

    @Container
    private static final DockerComposeContainer<?> compose = new DockerComposeContainer<>(
            new File("../docker-compose.yml"))
            .withLocalCompose(true)
            .withExposedService("kafka", 9092)
            .withExposedService("zookeeper", 2181)
            .withExposedService("krakend", 8000)
            .withExposedService("rental-room-service", 8081)
            .withExposedService("payment-service", 8083)
            .withExposedService("listing-service", 8082)
            .withExposedService("notification-service", 8087)
            .withOptions("--compatibility")
            .withPull(true);

    @BeforeAll
    static void setUp() {
        // Start Kafka first
        kafka.start();
        
        // Wait for Kafka to be ready
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting for Kafka", e);
        }
        
        // Start other services
        compose.start();
        
        // Wait for services to be ready
        try {
            Thread.sleep(70000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting for services", e);
        }
        
        RestAssured.baseURI = "http://localhost:8000";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.config().getLogConfig().enablePrettyPrinting(true);
    }

    @Test
    @Order(1)
    void shouldCreateListingAndMakeReservation() {
        // 1. Create a new listing
        Map<String, Object> listingRequest = new HashMap<>();
        listingRequest.put("title", "Test Room");
        listingRequest.put("description", "A beautiful test room");
        listingRequest.put("pricePerNight", 100.0);
        listingRequest.put("nrOfRooms", 2);
        listingRequest.put("maxGuests", 4);
        
        Map<String, Object> address = new HashMap<>();
        address.put("street", "Test Street");
        address.put("city", "Test City");
        address.put("country", "Test Country");
        address.put("postalCode", "12345");
        address.put("houseNumber", "42");
        listingRequest.put("address", address);

        System.out.println("Request body: " + listingRequest);
        Integer listingId = given()
                .contentType(ContentType.JSON)
                .body(listingRequest)
                .when()
                .post("/listings/add")
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .path("id");

        // 2. Create a reservation
        Map<String, Object> reservationRequest = new HashMap<>();
        reservationRequest.put("guestName", "Jan Kowalski");
        reservationRequest.put("guestEmail", "jan.kowalski@example.com");
        reservationRequest.put("checkInDate", "2025-06-15");
        reservationRequest.put("checkOutDate", "2025-06-25");

        System.out.println("Reservation request body: " + reservationRequest);
        Integer reservationId = given()
                .contentType(ContentType.JSON)
                .body(reservationRequest)
                .when()
                .post("/reservations/room/" + listingId)
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .path("id");
    }

    @Test
    @Order(2)
    void shouldCancelReservation() {
        // 1. Create a reservation
        Map<String, Object> reservationRequest = new HashMap<>();
        reservationRequest.put("guestName", "Jan Kowalski");
        reservationRequest.put("guestEmail", "jan.kowalski@example.com");
        reservationRequest.put("checkInDate", "2025-07-15");
        reservationRequest.put("checkOutDate", "2025-07-25");

        System.out.println("Reservation request body: " + reservationRequest);
        Integer reservationId = given()
                .contentType(ContentType.JSON)
                .body(reservationRequest)
                .when()
                .post("/reservations/room/1")
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .path("id");

        // 2. Cancel the reservation
        System.out.println("Cancelling reservation with ID: " + reservationId);
        given()
                .contentType(ContentType.JSON)
                .queryParam("cancellerType", "GUEST")
                .when()
                .post("/reservations/cancel/" + reservationId)
                .then()
                .log().all()
                .statusCode(200)
                .body("cancellerType", equalTo("GUEST"));

        // 3. Verify reservation status
        System.out.println("Verifying reservation status for ID: " + reservationId);
        given()
                .when()
                .get("/reservations/" + reservationId)
                .then()
                .log().all()
                .statusCode(200)
                .body("status", equalTo("CANCELLED_BY_GUEST"));
    }
} 