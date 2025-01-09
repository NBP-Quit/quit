package com.quit.reservation.infrastructure.messaging.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMessage {
    private UUID paymentId;
    private Integer amount;
    private UUID reservationId;
}
