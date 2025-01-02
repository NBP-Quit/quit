package com.quit.store.domain.repository;

import com.quit.store.domain.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StoreRepository extends JpaRepository<Store, UUID> {

    Optional<Store> findByIdAndIsDeleteFalse(UUID storeId);
}
