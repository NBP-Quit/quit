package com.quit.store.application.service;

import com.quit.store.application.dto.ReservationSlotDto;
import com.quit.store.application.dto.res.ReservationSlotResponse;
import com.quit.store.domain.entity.ReservationSlot;
import com.quit.store.domain.entity.Store;
import com.quit.store.domain.repository.ReservationSlotRepository;
import com.quit.store.domain.repository.StoreRepository;
import com.quit.store.presentation.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.quit.store.presentation.exception.ErrorType.STORE_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationSlotService {

    private final ReservationSlotRepository reservationSlotRepository;
    private final StoreRepository storeRepository;

    public ReservationSlotResponse createSlot(UUID storeId, ReservationSlotDto request, String userId) {
        // todo: 권한체크로직
        Store store = checkStore(storeId);
        ReservationSlot slot = create(store, request);
        reservationSlotRepository.save(slot);
        return ReservationSlotResponse.from(slot);
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

}
