package com.quit.payment.domain.entity;

import com.quit.payment.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_payment")
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "payment_key", nullable = false)
    private String paymentKey;

    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(name = "cancel_reason")
    private String cancelReason;

    @Column(name = "reservation_id", nullable = false, unique = true)
    private UUID reservationId;

    @Builder
    private Payment(Integer amount, Status status,
                    String paymentKey, String orderId, UUID reservationId) {
        this.amount = amount;
        this.status = status;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.reservationId = reservationId;
    }

    public static Payment of(Integer amount, Status status,
                             String paymentKey, String orderId, UUID reservationId) {
        return Payment.builder()
                .amount(amount)
                .status(status)
                .paymentKey(paymentKey)
                .orderId(orderId)
                .reservationId(reservationId)
                .build();
    }

    public void cancel(Status status, String cancelReason) {
        updateStatus(status);
        updateCancelReason(cancelReason);
    }

    private void updateStatus(Status status) {
        if(status != null) {
            this.status = status;
        }
    }

    private void updateCancelReason(String cancelReason) {
        if(cancelReason != null) {
            this.cancelReason = cancelReason;
        }
    }

}
