package com.quit.store.domain.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.quit.store.domain.entity.ReservationSlot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.quit.store.domain.entity.QReservationSlot.reservationSlot;

@Slf4j
@RequiredArgsConstructor
public class ReservationSlotRepositoryImpl implements ReservationSlotRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<ReservationSlot> findByStoreId(UUID storeId, Pageable pageable) {

        List<ReservationSlot> slotList = jpaQueryFactory
                .selectFrom(reservationSlot)
                .where(
                        storeEq(storeId),
                        reservationSlot.isAvailable.eq(true),
                        reservationSlot.isDeleted.eq(false)
                )
                .orderBy(reservationSlot.store.id.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(reservationSlot.count())
                .from(reservationSlot)
                .where(
                        storeEq(storeId),
                        reservationSlot.isAvailable.eq(true),
                        reservationSlot.isDeleted.eq(false)
                );

        return PageableExecutionUtils.getPage(slotList, pageable, countQuery::fetchOne);
    }

    @Override
    public Optional<ReservationSlot> findByDateAndTime(UUID storeId, LocalDate date, LocalTime time) {
        ReservationSlot slot = jpaQueryFactory
                .selectFrom(reservationSlot)
                .where(
                        storeEq(storeId),
                        dateEq(date),
                        timeEq(time),
                        reservationSlot.isAvailable.eq(true),
                        reservationSlot.isDeleted.eq(false)
                )
                .fetchOne();
        return Optional.ofNullable(slot);
    }

    private BooleanExpression timeEq(LocalTime time) {
        return time != null ? reservationSlot.time.eq(time) : null;
    }

    private BooleanExpression dateEq(LocalDate date) {
        return date != null ? reservationSlot.date.eq(date) : null;
    }

    private BooleanExpression storeEq(UUID storeId) {
        return storeId != null ? reservationSlot.store.id.eq(storeId) : null;
    }

}
