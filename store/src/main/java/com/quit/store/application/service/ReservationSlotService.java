package com.quit.store.application.service;

import com.quit.store.application.dto.ReservationEvent;
import com.quit.store.application.dto.ReservationSlotDto;
import com.quit.store.application.dto.UpdateReservationSlotDto;
import com.quit.store.application.dto.res.ReservationSlotResponse;
import com.quit.store.domain.entity.ReservationSlot;
import com.quit.store.domain.entity.Store;
import com.quit.store.domain.repository.ReservationSlotRepository;
import com.quit.store.domain.repository.StoreRepository;
import com.quit.store.presentation.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static com.quit.store.presentation.exception.ErrorType.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReservationSlotService {

    private final ReservationSlotRepository reservationSlotRepository;
    private final StoreRepository storeRepository;

    @Transactional
    public ReservationSlotResponse createSlot(UUID storeId, ReservationSlotDto request, String userId) {
        // todo: 권한체크로직
        Store store = checkStore(storeId);
        ReservationSlot slot = create(store, request);
        reservationSlotRepository.save(slot);
        return ReservationSlotResponse.from(slot);
    }

    @Transactional
    public ReservationSlotResponse updateSlot(UUID storeId, UUID slotId,
                                              UpdateReservationSlotDto request, String userId) {
        // todo: 권한체크로직
        Store store = checkStore(storeId);
        ReservationSlot slot = checkSlot(slotId);
        validateSlotBelongsToStore(store.getId(), slot);
        // 1. 현재 currentCapacity > 0 일 때 날짜, 시간 변경 X
        // 2. maxCapacity 가 현재 currentCapacity 보다 작을 경우 변경 X
        validateDate(slot, request.getDate());
        validateTime(slot, request.getTime());
        validateMaxCapacity(slot, request.getMaxCapacity());
        slot.update(request);
        return ReservationSlotResponse.from(slot);
    }

    @Transactional(readOnly = true)
    public Page<ReservationSlotResponse> getSlotsByStore(UUID storeId, Pageable pageable) {
        Store store = checkStore(storeId);
        Page<ReservationSlot> reservationSlotPage = reservationSlotRepository.findByStoreId(store.getId(), pageable);
        return reservationSlotPage.map(ReservationSlotResponse::from);
    }

    @Transactional(readOnly = true)
    public ReservationSlotResponse getSlotByDateAndTime(UUID storeId, LocalDate date, LocalTime time) {
        Store store = checkStore(storeId);
        ReservationSlot slot = reservationSlotRepository.findByDateAndTime(store.getId(), date, time)
                .orElseThrow(() -> new CustomException(RESERVATION_SLOT_NOT_FOUND));
        return ReservationSlotResponse.from(slot);
    }

    @Transactional
    public void deleteSlot(UUID storeId, UUID slotId, String userId) {
        // todo: 권한체크로직
        Store store = checkStore(storeId);
        ReservationSlot slot = checkSlot(slotId);
        validateSlotBelongsToStore(store.getId(), slot);
        validateReservation(slot);
        slot.delete(userId);
    }

    @Transactional
    @KafkaListener(topics = "reservation.confirm.success", groupId = "reservation-slot", containerFactory = "kafkaReservationEventContainerFactory")
    public void increaseCapacity(ReservationEvent reservationEvent) {
        log.info(">>>>>>> increaseCapacity <<<<<<<<<");
        ReservationSlot reservationSlot = checkSlot(reservationEvent.getReservationSlotId());
        validateSlotIsAvailable(reservationSlot);
        validateCapacityLimit(reservationSlot, reservationEvent.getCurrentCapacity());
        reservationSlot.increaseCapacity(reservationEvent.getCurrentCapacity());
        log.info("<<<<<<< reservation slot increased <<<<<<<<<");
    }

    @Transactional
    @KafkaListener(topics = "reservation.confirm.failed", groupId = "reservation-slot", containerFactory = "kafkaReservationEventContainerFactory")
    public void restoreCapacity(ReservationEvent reservationEvent) {
        log.info(">>>>>>> restoreCapacity <<<<<<<<<");
        ReservationSlot reservationSlot = checkSlot(reservationEvent.getReservationSlotId());
        validateSlotIsAvailable(reservationSlot);
        validateSufficientCapacity(reservationSlot, reservationEvent.getCurrentCapacity());
        reservationSlot.restoreCapacity(reservationEvent.getCurrentCapacity());
        log.info("<<<<<<< reservation slot restored <<<<<<<<<");
    }

    private void validateSlotIsAvailable(ReservationSlot slot) {
        if (!slot.getIsAvailable()) {
            throw new CustomException(RESERVATION_SLOT_NOT_AVAILABLE);
        }
    }

    private void validateCapacityLimit(ReservationSlot slot, int increment) {
        if ((slot.getCurrentCapacity() + increment) > slot.getMaxCapacity()) {
            throw new CustomException(RESERVATION_SLOT_MAX_CAPACITY_LIMIT_EXCEEDED);
        }
    }

    private void validateSufficientCapacity(ReservationSlot slot, int decrement) {
        if ((slot.getCurrentCapacity() - decrement) < 0) {
            throw new CustomException(RESERVATION_SLOT_CURRENT_CAPACITY_INVALID);
        }
    }

    private void validateSlotBelongsToStore(UUID storeId, ReservationSlot slot) {
        if (!slot.getStore().getId().equals(storeId)) {
            throw new CustomException(RESERVATION_SLOT_STORE_MISMATCH);
        }
    }

    private void validateReservation(ReservationSlot slot) {
        if(slot.getCurrentCapacity() > 0) {
            throw new CustomException(RESERVATION_SLOT_DELETE_NOT_ALLOWED);
        }
    }

    private void validateMaxCapacity(ReservationSlot slot, Integer maxCapacity) {
        if(maxCapacity != null && maxCapacity < slot.getCurrentCapacity()) {
            throw new CustomException(RESERVATION_SLOT_MAX_CAPACITY_NOT_ALLOWED);
        }
    }

    private void validateTime(ReservationSlot slot, LocalTime time) {
        if(slot.getCurrentCapacity() > 0 && time != null && !time.equals(slot.getTime())) {
            throw new CustomException(RESERVATION_SLOT_TIME_NOT_ALLOWED);
        }
    }

    private void validateDate(ReservationSlot slot, LocalDate date) {
        if(slot.getCurrentCapacity() > 0 && date != null && !date.equals(slot.getDate())) {
            throw new CustomException(RESERVATION_SLOT_DATE_NOT_ALLOWED);
        }
    }

    private ReservationSlot create(Store store, ReservationSlotDto request) {
        return ReservationSlot.of(
                request.getDate(),
                request.getTime(),
                request.getMaxCapacity(),
                store
        );
    }

    private Store checkStore(UUID storeId) {
        return storeRepository.findByIdAndIsDeletedFalse(storeId)
                .orElseThrow(() -> new CustomException(STORE_NOT_FOUND));
    }

    private ReservationSlot checkSlot(UUID slotId) {
        return reservationSlotRepository.findByIdAndIsDeletedFalse(slotId)
                .orElseThrow(() -> new CustomException(RESERVATION_SLOT_NOT_FOUND));
    }

}
