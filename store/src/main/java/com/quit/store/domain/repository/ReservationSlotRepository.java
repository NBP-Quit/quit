package com.quit.store.domain.repository;

import com.quit.store.domain.entity.ReservationSlot;
import com.quit.store.domain.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ReservationSlotRepository extends JpaRepository<ReservationSlot, UUID> {

    Optional<ReservationSlot> findByIdAndIsDeletedFalse(UUID slotId);

    Page<ReservationSlot> findByStoreAndIsDeletedFalseAndIsAvailableTrue(Store store, Pageable pageable);

}
