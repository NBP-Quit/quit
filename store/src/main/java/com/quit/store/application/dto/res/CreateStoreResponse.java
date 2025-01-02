package com.quit.store.application.dto.res;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateStoreResponse {
    private UUID storeId;

    @Builder
    private CreateStoreResponse(UUID storeId) {
        this.storeId = storeId;
    }

    public static CreateStoreResponse from(UUID storeId) {
        return CreateStoreResponse.builder().storeId(storeId).build();
    }

}
