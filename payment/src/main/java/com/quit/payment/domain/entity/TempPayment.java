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
@Table(name = "temp_payment")
public class TempPayment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Builder
    private TempPayment(Integer amount, String orderId) {
        this.amount = amount;
        this.orderId = orderId;
    }

    public static TempPayment of(Integer amount, String orderId) {
        return TempPayment.builder()
                .amount(amount)
                .orderId(orderId)
                .build();
    }

}
