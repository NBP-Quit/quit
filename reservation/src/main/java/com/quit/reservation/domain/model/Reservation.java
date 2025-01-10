package com.quit.reservation.domain.model;

import com.quit.reservation.common.model.BaseEntity;
import com.quit.reservation.domain.enums.ReservationStatus;
import com.quit.reservation.presentation.exception.CustomException;
import com.quit.reservation.presentation.exception.error.ErrorType;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Getter
@Table(name = "p_reservation")
@AllArgsConstructor
@NoArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class Reservation extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID reservationId;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @Column(name = "store_id", nullable = false)
    private UUID storeId;

    @Column(name = "guest_count", nullable = false)
    private Integer guestCount;

    @Column(name = "reservation_date", nullable = false)
    private LocalDate reservationDate;

    @Column(name = "reservation_time", nullable = false)
    private LocalTime reservationTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "reservation_status", nullable = false)
    private ReservationStatus reservationStatus;

    @Column(name = "reservation_price")
    private Integer reservationPrice;

    @Column(name = "slot_id")
    private UUID slotId;

    public static Reservation create(String customerId,
                                     UUID storeId,
                                     Integer guestCount,
                                     LocalDate reservationDate,
                                     LocalTime reservationTime,
                                     ReservationStatus reservationStatus,
                                     Integer reservationPrice,
                                     UUID slotId) {

        return Reservation.builder()
                .customerId(customerId)
                .storeId(storeId)
                .guestCount(guestCount)
                .reservationDate(reservationDate)
                .reservationTime(reservationTime)
                .reservationStatus(reservationStatus)
                .reservationPrice(reservationPrice)
                .slotId(slotId)
                .build();
    }

    public void changeStatus(ReservationStatus reservationStatus) {
        this.reservationStatus = reservationStatus;
    }

    public void cancel() {
        this.reservationStatus = ReservationStatus.CANCELED;
    }

    public void updateReservationPrice(Integer reservationPrice) {
        this.reservationPrice = reservationPrice;
    }
}
