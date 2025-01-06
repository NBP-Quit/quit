package com.quit.reservation.infrastructure.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.quit.reservation.domain.enums.Role;
import com.quit.reservation.domain.model.Reservation;
import com.quit.reservation.domain.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

import static com.quit.reservation.domain.model.QReservation.reservation;

@Repository
@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final ReservationJpaRepository reservationJpaRepository;

    @Override
    public Reservation save(Reservation reservation) {
        return reservationJpaRepository.save(reservation);
    }

    @Override
    public Optional<Reservation> findByReservationIdIsDeletedFalse(UUID reservationId) {
        JPAQuery<Reservation> query = jpaQueryFactory
                .select(reservation)
                .from(reservation)
                .where(reservation.isDeleted.eq(false))
                .where(reservation.reservationId.eq(reservationId));

        return Optional.ofNullable(query.fetchOne());
    }

    @Override
    public Page<Reservation> findReservationsByUser(String customerId, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(reservation.isDeleted.eq(false));
        builder.and(reservation.customerId.eq(customerId));
        return reservationJpaRepository.findAll(builder, pageable);
    }

    @Override
    public Page<Reservation> findReservationsByStore(UUID storeId, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(reservation.isDeleted.eq(false));
        builder.and(reservation.storeId.eq(storeId));
        return reservationJpaRepository.findAll(builder, pageable);
    }

    @Override
    public Page<Reservation> findAllReservations(Role role, Predicate predicate, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder(predicate);
        builder.and(reservation.isDeleted.eq(false));
        return reservationJpaRepository.findAll(builder, pageable);
    }
}
