package com.quit.store.application.service;

import com.quit.store.application.dto.StoreDto;
import com.quit.store.application.dto.res.CreateStoreResponse;
import com.quit.store.domain.entity.Store;
import com.quit.store.domain.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
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

}
