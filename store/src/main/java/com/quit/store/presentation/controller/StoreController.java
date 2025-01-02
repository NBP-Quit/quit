package com.quit.store.presentation.controller;

import com.quit.store.application.dto.res.CreateStoreResponse;
import com.quit.store.application.dto.res.UpdateStoreResponse;
import com.quit.store.application.service.StoreService;
import com.quit.store.common.dto.ApiResponse;
import com.quit.store.presentation.dto.CreateStoreRequest;
import com.quit.store.presentation.dto.UpdateStoreRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores")
public class StoreController {

    private final StoreService storeService;

    @PostMapping
    public ResponseEntity<ApiResponse<CreateStoreResponse>> createStore(@Valid @RequestBody CreateStoreRequest request) {
        // todo:
        //  1. 게이트웨이 구현 후 @RequestHeader(name = "X-user-id") String userId 로 수정
        //  2. @RequestHeader(name = "X-user-role") String userRole 추가

        String userId = "testUserId";
        CreateStoreResponse response = storeService.createStore(request.toDto(), userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{storeId}")
    public ResponseEntity<ApiResponse<UpdateStoreResponse>> updateStore(@PathVariable(name = "storeId") UUID storeId,
                                                                        @RequestBody UpdateStoreRequest request) {
        // todo:
        //  1. 게이트웨이 구현 후 @RequestHeader(name = "X-user-id") String userId 로 수정
        //  2. @RequestHeader(name = "X-user-role") String userRole 추가

        String userId = "testUserId";
        UpdateStoreResponse response = storeService.updateStore(storeId, request.toDto(), userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }



}
