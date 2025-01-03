package com.quit.reservation.domain.model;

import com.quit.reservation.domain.enums.ReservationStatus;
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
public class Reservation implements Serializable {

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

    @Version
    private Integer version;

    //TODO: isDeleted 필드 및 삭제 메서드 임시 사용 - Base Entity 연결 후 삭제 예정
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;
    public void markDeleted() {
        this.isDeleted = true;
    }
    @PrePersist
    private void prePersistence() {
        if (isDeleted == null) {
            isDeleted = false;
        }
    }

    public static Reservation create(String customerId,
                                     UUID storeId,
                                     Integer guestCount,
                                     LocalDate reservationDate,
                                     LocalTime reservationTime,
                                     ReservationStatus reservationStatus,
                                     Integer reservationPrice) {

        return Reservation.builder()
                .customerId(customerId)
                .storeId(storeId)
                .guestCount(guestCount)
                .reservationDate(reservationDate)
                .reservationTime(reservationTime)
                .reservationStatus(reservationStatus)
                .reservationPrice(reservationPrice)
                .build();
    }

    public void changeStatus(ReservationStatus reservationStatus) {
        this.reservationStatus = reservationStatus;
    }

    public void cancel() {
        this.reservationStatus = ReservationStatus.CANCELED;
    }

    public void updateDetails(Integer guestCount, LocalDate reservationDate, LocalTime reservationTime) {
        this.guestCount = guestCount;
        this.reservationDate = reservationDate;
        this.reservationTime = reservationTime;
    }
}
