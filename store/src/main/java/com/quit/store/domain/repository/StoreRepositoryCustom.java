package com.quit.store.domain.repository;

import com.quit.store.application.dto.SearchStoreDto;
import com.quit.store.domain.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StoreRepositoryCustom {
    Page<Store> findAllBySearchRequest(SearchStoreDto request, Pageable pageable);
}
