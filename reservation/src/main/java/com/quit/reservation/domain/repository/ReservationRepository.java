package com.quit.reservation.domain.repository;

import com.quit.reservation.domain.model.Reservation;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository {
    Reservation save(Reservation reservation);
}
