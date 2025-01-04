package com.quit.store.domain.entity;

import com.quit.store.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_reservation_slot")
public class ReservationSlot extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "time", nullable = false)
    private LocalTime time;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;

    @Column(name = "max_capacity", nullable = false)
    private Integer maxCapacity;

    @Column(name = "current_capacity", nullable = false)
    private Integer currentCapacity = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Builder
    private ReservationSlot(LocalDate date, LocalTime time,
                            Integer maxCapacity, Store store) {
        this.date = date;
        this.time = time;
        this.maxCapacity = maxCapacity;
        this.store = store;
    }

    public static ReservationSlot of(LocalDate date, LocalTime time,
                                     Integer maxCapacity, Store store) {
        return ReservationSlot.builder()
                .date(date)
                .time(time)
                .maxCapacity(maxCapacity)
                .store(store)
                .build();
    }

}
