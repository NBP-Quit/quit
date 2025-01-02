package com.quit.store.domain.entity;

import com.quit.store.application.dto.StoreDto;
import com.quit.store.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_store")
public class Store extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "contact_number", nullable = false)
    private String contactNumber;

    @Column(name = "reservation_deposit", nullable = false)
    private Integer reservationDeposit;

    @Column(name = "open_time", nullable = false)
    private LocalTime openTime;

    @Column(name = "close_time", nullable = false)
    private LocalTime closeTime;

    @Column(name = "last_order_time", nullable = false)
    private LocalTime lastOrderTime;

    @Column(name = "category", nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Builder
    private Store(String name, String description, String address,
                  String contactNumber, Integer reservationDeposit,
                  LocalTime openTime, LocalTime closeTime,
                  LocalTime lastOrderTime, Category category, String userId) {
        this.name = name;
        this.description = description;
        this.address = address;
        this.contactNumber = contactNumber;
        this.reservationDeposit = reservationDeposit;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.lastOrderTime = lastOrderTime;
        this.category = category;
        this.userId = userId;
    }

    public static Store of(String name, String description, String address,
                           String contactNumber, Integer reservationDeposit,
                           LocalTime openTime, LocalTime closeTime,
                           LocalTime lastOrderTime, Category category, String userId) {
        return Store.builder()
                .name(name)
                .description(description)
                .address(address)
                .contactNumber(contactNumber)
                .reservationDeposit(reservationDeposit)
                .openTime(openTime)
                .closeTime(closeTime)
                .lastOrderTime(lastOrderTime)
                .category(category)
                .userId(userId)
                .build();
    }

    public void update(StoreDto request) {
        updateName(request.getName());
        updateDescription(request.getDescription());
        updateAddress(request.getAddress());
        updateContactNumber(request.getContactNumber());
        updateReservationDeposit(request.getReservationDeposit());
        updateOpenTime(request.getOpenTime());
        updateCloseTime(request.getCloseTime());
        updateLastOrderTime(request.getLastOrderTime());
        updateCategory(request.getCategory());
    }

    private void updateName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
        }
    }

    private void updateDescription(String description) {
        if(description != null && !description.trim().isEmpty()) {
            this.description = description;
        }
    }

    private void updateAddress(String address) {
        if (address != null && !address.trim().isEmpty()) {
            this.address = address;
        }
    }

    private void updateContactNumber(String contactNumber) {
        if (contactNumber != null && !contactNumber.trim().isEmpty()) {
            this.contactNumber = contactNumber;
        }
    }

    private void updateReservationDeposit(Integer reservationDeposit) {
        if (reservationDeposit != null && reservationDeposit > 0) {
            this.reservationDeposit = reservationDeposit;
        }
    }

    private void updateOpenTime(LocalTime openTime) {
        if (openTime != null) {
            this.openTime = openTime;
        }
    }

    private void updateCloseTime(LocalTime closeTime) {
        if (closeTime != null) {
            this.closeTime = closeTime;
        }
    }

    private void updateLastOrderTime(LocalTime lastOrderTime) {
        if (lastOrderTime != null) {
            this.lastOrderTime = lastOrderTime;
        }
    }

    private void updateCategory(Category category) {
        if (category != null) {
            this.category = category;
        }
    }

}
