package com.quit.store.domain.repository;

import com.quit.store.domain.entity.ReservationSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReservationSlotRepository extends JpaRepository<ReservationSlot, UUID> {
}
