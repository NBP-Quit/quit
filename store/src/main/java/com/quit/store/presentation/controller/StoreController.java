package com.quit.store.presentation.controller;

import com.quit.store.application.dto.StoreDto;
import com.quit.store.application.dto.res.CreateStoreResponse;
import com.quit.store.application.service.StoreService;
import com.quit.store.common.dto.ApiResponse;
import com.quit.store.presentation.dto.CreateStoreRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
