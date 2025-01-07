package com.quit.reservation.domain.repository;

import com.querydsl.core.types.Predicate;
import com.quit.reservation.domain.enums.Role;
import com.quit.reservation.domain.model.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReservationRepository {
    Reservation save(Reservation reservation);

    Optional<Reservation> findByReservationIdIsDeletedFalse(UUID reservationId);

    Page<Reservation> findReservationsByUser(String customerId, Pageable pageable);

    Page<Reservation> findReservationsByStore(UUID storeId, Pageable pageable);

    Page<Reservation> findAllReservations(Role role, Predicate predicate, Pageable pageable);
}
