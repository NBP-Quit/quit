package com.quit.payment.application.dto.res;

import com.quit.payment.domain.entity.Payment;
import com.quit.payment.domain.entity.Status;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentResponse {

    private UUID id;
    private Integer amount;
    private Status status;
    private String paymentKey;
    private String orderId;
    private UUID reservationId;
    private String cancelReason;

    @Builder
    private PaymentResponse(UUID id, Integer amount, Status status,
                           String paymentKey, String orderId,
                            UUID reservationId, String cancelReason) {
        this.id = id;
        this.amount = amount;
        this.status = status;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.reservationId = reservationId;
        this.cancelReason = cancelReason;
    }

    public static PaymentResponse from(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .paymentKey(payment.getPaymentKey())
                .orderId(payment.getOrderId())
                .reservationId(payment.getReservationId())
                .cancelReason(payment.getCancelReason())
                .build();
    }

}
