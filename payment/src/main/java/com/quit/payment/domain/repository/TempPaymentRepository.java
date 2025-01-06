package com.quit.payment.domain.repository;

import com.quit.payment.domain.entity.TempPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TempPaymentRepository extends JpaRepository<TempPayment, UUID> {
    Optional<TempPayment> findByOrderId(String orderId);
}
