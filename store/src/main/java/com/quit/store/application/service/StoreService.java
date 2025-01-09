package com.quit.store.application.service;

import com.quit.store.application.dto.SearchStoreDto;
import com.quit.store.application.dto.StoreDto;
import com.quit.store.application.dto.res.CreateStoreResponse;
import com.quit.store.application.dto.res.StoreResponse;
import com.quit.store.common.util.RoleValidator;
import com.quit.store.domain.entity.Store;
import com.quit.store.domain.repository.StoreRepository;
import com.quit.store.presentation.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.quit.store.common.util.RoleValidator.Action.*;
import static com.quit.store.presentation.exception.ErrorType.*;


@Service
@RequiredArgsConstructor
@Transactional
public class StoreService {

    private final StoreRepository storeRepository;
    private final RoleValidator roleValidator;

    public CreateStoreResponse createStore(StoreDto request, String userId, String userRole) {
        roleValidator.validateRole(userRole, CREATE);
        Store store = create(request, userId);
        storeRepository.save(store);
        return CreateStoreResponse.from(store.getId());
    }

    public StoreResponse updateStore(UUID storeId, StoreDto request, String userId, String userRole) {
        roleValidator.validateRole(userRole, UPDATE);
        Store store = checkStore(storeId);
        checkUser(store, userId, userRole);
        store.update(request);
        return StoreResponse.from(store);
    }

    @Transactional(readOnly = true)
    public StoreResponse getStore(UUID storeId) {
        Store store = checkStore(storeId);
        return StoreResponse.from(store);
    }

    @Transactional(readOnly = true)
    public Page<StoreResponse> searchStores(SearchStoreDto request, Pageable pageable) {
        Page<Store> storePage =  storeRepository.findAllBySearchRequest(request, pageable);
        return storePage.map(StoreResponse::from);
    }

    public void deleteStore(UUID storeId, String userId, String userRole) {
        roleValidator.validateRole(userRole, STORE_DELETE);
        Store store = checkStore(storeId);
        store.delete(userId);
    }

    private Store checkStore(UUID storeId) {
        return storeRepository.findByIdAndIsDeletedFalse(storeId)
                .orElseThrow(() -> new CustomException(STORE_NOT_FOUND));
    }

    private void checkUser(Store store, String userId, String userRole) {
        if (userRole.equals("ROLE_OWNER")) {
            if (!store.getUserId().equals(userId)) {
                throw new CustomException(USER_NOT_SAME);
            }
        }
    }

    private Store create(StoreDto request, String userId) {
        return Store.of(
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
    }

}
