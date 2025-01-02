package com.quit.store.application.service;

import com.quit.store.application.dto.StoreDto;
import com.quit.store.application.dto.res.CreateStoreResponse;
import com.quit.store.application.dto.res.StoreResponse;
import com.quit.store.domain.entity.Store;
import com.quit.store.domain.repository.StoreRepository;
import com.quit.store.presentation.exception.CustomException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.quit.store.presentation.exception.ErrorType.STORE_NOT_FOUND;
import static com.quit.store.presentation.exception.ErrorType.USER_NOT_SAME;


@Service
@RequiredArgsConstructor
@Transactional
public class StoreService {

    private final StoreRepository storeRepository;

    public CreateStoreResponse createStore(StoreDto request, String userId) {
        //todo: 권한체크로직
        Store store = Store.of(
                request.getName(),
                request.getDescription(),
                request.getAddress(),
                request.getContactNumber(),
                request.getReservationDeposit(),
                request.getOpenTime(),
                request.getCloseTime(),
                request.getLastOrderTime(),
                request.getCategory(),
                userId
        );
        storeRepository.save(store);
        return CreateStoreResponse.from(store.getId());
    }

    public StoreResponse updateStore(UUID storeId, StoreDto request, String userId) {
        //todo: 권한체크로직
        Store store = CheckStore(storeId);
        CheckUser(store, userId);
        store.update(request);
        return StoreResponse.from(store);
    }

    @Transactional(readOnly = true)
    public StoreResponse getStore(UUID storeId) {
        Store store = CheckStore(storeId);
        return StoreResponse.from(store);
    }

    public void deleteStore(UUID storeId, String userId) {
        //todo: 권한체크로직
        Store store = CheckStore(storeId);
        store.delete(userId);
    }

    private Store CheckStore(UUID storeId) {
        return storeRepository.findByIdAndIsDeleteFalse(storeId)
                .orElseThrow(() -> new CustomException(STORE_NOT_FOUND));
    }

    private void CheckUser(Store store, String userId) {
        if(!store.getUserId().equals(userId)) {
            throw new CustomException(USER_NOT_SAME);
        }
    }

}
