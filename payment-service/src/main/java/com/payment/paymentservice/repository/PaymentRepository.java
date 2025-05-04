package com.payment.paymentservice.repository;

import com.payment.paymentservice.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPayuOrderId(String payuOrderId);
}
