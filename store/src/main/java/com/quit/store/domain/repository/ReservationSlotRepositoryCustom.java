package com.quit.store.domain.repository;

import com.quit.store.domain.entity.ReservationSlot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

public interface ReservationSlotRepositoryCustom {
    Page<ReservationSlot> findByStoreId(UUID storeId, Pageable pageable);
    Optional<ReservationSlot> findByDateAndTime(UUID storeId, LocalDate date, LocalTime time);
}
