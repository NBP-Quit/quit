package com.quit.reservation.infrastructure.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.StringPath;
import com.quit.reservation.domain.enums.Role;
import com.quit.reservation.domain.model.QReservation;
import com.quit.reservation.domain.model.Reservation;
import com.quit.reservation.domain.repository.ReservationRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

import java.util.*;

import static com.quit.reservation.domain.model.QReservation.reservation;

public interface ReservationJpaRepository extends JpaRepository<Reservation, UUID>,
        ReservationRepository,
        QuerydslPredicateExecutor<Reservation>,
        QuerydslBinderCustomizer<QReservation> {

    @Override
    default void customize(QuerydslBindings querydslBindings, @NotNull QReservation reservation) {
        querydslBindings.bind(String.class).all((StringPath path, Collection<? extends String> values) -> {
            List<String> valueList = new ArrayList<>(values.stream().map(String::trim).toList());
            if (valueList.isEmpty()) {
                return Optional.empty();
            }
            BooleanBuilder booleanBuilder = new BooleanBuilder();
            for (String s : valueList) {
                booleanBuilder.or(path.containsIgnoreCase(s));
            }
            return Optional.of(booleanBuilder);
        });
    }

    @Override
    default Optional<Reservation> findByReservationIdIsDeletedFalse(UUID reservationId) {
        return findOne(
                reservation.isDeleted.eq(false).and(reservation.reservationId.eq(reservationId))
        );
    }


    @Override
    default Page<Reservation> findReservationsByUser(String customerId, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(reservation.isDeleted.eq(false));
        builder.and(reservation.customerId.eq(customerId));
        return findAll(builder, pageable);
    }

    @Override
    default Page<Reservation> findReservationsByStore(UUID storeId, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(reservation.isDeleted.eq(false));
        builder.and(reservation.storeId.eq(storeId));
        return findAll(builder, pageable);
    }

    @Override
    default Page<Reservation> findAllReservations(Role role, Predicate predicate, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder(predicate);
        builder.and(reservation.isDeleted.eq(false));
        return findAll(builder, pageable);
    }
}
