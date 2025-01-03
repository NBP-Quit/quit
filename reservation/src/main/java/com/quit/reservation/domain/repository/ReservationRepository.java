package com.quit.reservation.domain.repository;

import com.quit.reservation.domain.model.Reservation;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReservationRepository {
    Reservation save(Reservation reservation);

    Optional<Reservation> findByReservationId(UUID reservationId);
}
