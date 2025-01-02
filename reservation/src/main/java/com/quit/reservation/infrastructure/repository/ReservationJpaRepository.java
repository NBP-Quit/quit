package com.quit.reservation.infrastructure.repository;

import com.quit.reservation.domain.model.Reservation;
import com.quit.reservation.domain.repository.ReservationRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReservationJpaRepository extends JpaRepository<Reservation, UUID>, ReservationRepository {
}
