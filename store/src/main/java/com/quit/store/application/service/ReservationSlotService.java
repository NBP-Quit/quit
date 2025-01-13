package com.quit.store.application.service;

import com.quit.store.application.dto.BatchReservationSlotsDto;
import com.quit.store.application.dto.ReservationEvent;
import com.quit.store.application.dto.ReservationSlotDto;
import com.quit.store.application.dto.UpdateReservationSlotDto;
import com.quit.store.application.dto.res.ReservationSlotResponse;
import com.quit.store.common.util.RoleValidator;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.quit.store.common.util.RoleValidator.Action.*;
import static com.quit.store.presentation.exception.ErrorType.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReservationSlotService {

    private final ReservationSlotRepository reservationSlotRepository;
    private final StoreRepository storeRepository;
    private final RoleValidator roleValidator;

    @Transactional
    public ReservationSlotResponse createSingleSlot(UUID storeId, ReservationSlotDto request, String userId, String userRole) {
        roleValidator.validateRole(userRole, CREATE);
        Store store = checkStore(storeId);
        checkUser(store, userId, userRole);
        ReservationSlot slot = create(store, request);
        reservationSlotRepository.save(slot);
        return ReservationSlotResponse.from(slot);
    }

    @Transactional
    public void createBatchSlots(UUID storeId, BatchReservationSlotsDto request, String userId, String userRole) {
        roleValidator.validateRole(userRole, CREATE);
        Store store = checkStore(storeId);
        checkUser(store, userId, userRole);
        // 기존 슬롯을 조회하여 중복을 방지
        List<ReservationSlot> existingSlots = findExistingSlots(storeId, request.getStartDate(), request.getEndDate());
        Set<String> existingSlotKeys = generateSlotKeys(existingSlots);
        List<ReservationSlot> newSlots = generateBatchSlots(request, existingSlotKeys, store);
        reservationSlotRepository.saveAll(newSlots);
    }

    @Transactional
    public ReservationSlotResponse updateSlot(UUID storeId, UUID slotId,
                                              UpdateReservationSlotDto request,
                                              String userId, String userRole) {
        roleValidator.validateRole(userRole, UPDATE);
        Store store = checkStore(storeId);
        checkUser(store, userId, userRole);
        ReservationSlot slot = checkSlot(slotId);
        validateSlotBelongsToStore(store.getId(), slot);
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
    public void deleteSlot(UUID storeId, UUID slotId, String userId, String userRole) {
        roleValidator.validateRole(userRole, SLOT_DELETE);
        Store store = checkStore(storeId);
        checkUser(store, userId, userRole);
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

    private Set<String> generateSlotKeys(List<ReservationSlot> existingSlots) {
        return existingSlots.stream()
                .map(slot -> slot.getDate().toString() + "_" + slot.getTime().toString())
                .collect(Collectors.toSet());
    }

    private List<ReservationSlot> findExistingSlots(UUID storeId, LocalDate startDate, LocalDate endDate) {
        return reservationSlotRepository.findAllByStoreIdAndDateRange(storeId, startDate, endDate);
    }

    private List<ReservationSlot> generateBatchSlots(BatchReservationSlotsDto request, Set<String> existingSlotKeys, Store store) {
        List<ReservationSlot> newSlots = new ArrayList<>();
        for (LocalDate date = request.getStartDate(); !date.isAfter(request.getEndDate()); date = date.plusDays(1)) {
            for (LocalTime time = request.getStartTime(); !time.isAfter(request.getEndTime()); time = time.plusMinutes(request.getInterval())) {
                String key = date + "_" + time;
                if (!existingSlotKeys.contains(key)) {
                    newSlots.add(ReservationSlot.of(date, time, request.getMaxCapacity(), store));
                }
            }
        }
        return newSlots;
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

    private void checkUser(Store store, String userId, String userRole) {
        if (userRole.equals("ROLE_OWNER")) {
            if (!store.getUserId().equals(userId)) {
                throw new CustomException(USER_NOT_SAME);
            }
        }
    }

}
