package com.rental;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication()
@EnableWebSecurity
public class RentalRoomServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RentalRoomServiceApplication.class, args);
    }
}