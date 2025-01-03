package com.quit.store.presentation.controller;

import com.quit.store.application.dto.res.CreateStoreResponse;
import com.quit.store.application.dto.res.StoreResponse;
import com.quit.store.application.service.StoreService;
import com.quit.store.common.dto.ApiResponse;
import com.quit.store.common.util.PageableUtil;
import com.quit.store.presentation.dto.CreateStoreRequest;
import com.quit.store.presentation.dto.SearchStoreRequest;
import com.quit.store.presentation.dto.UpdateStoreRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<ApiResponse<StoreResponse>> updateStore(@PathVariable(name = "storeId") UUID storeId,
                                                                  @RequestBody UpdateStoreRequest request) {
        // todo:
        //  1. 게이트웨이 구현 후 @RequestHeader(name = "X-user-id") String userId 로 수정
        //  2. @RequestHeader(name = "X-user-role") String userRole 추가

        String userId = "testUserId";
        StoreResponse response = storeService.updateStore(storeId, request.toDto(), userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<ApiResponse<StoreResponse>> getStore(@PathVariable(name = "storeId") UUID storeId) {
        return ResponseEntity.ok(ApiResponse.success(storeService.getStore(storeId)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<StoreResponse>>> searchStores(@RequestBody SearchStoreRequest request,
                                                                        @RequestParam(defaultValue = "1") int page,
                                                                        @RequestParam(defaultValue = "10") int size,
                                                                        @RequestParam(defaultValue = "createdAt") String sortBy,
                                                                        @RequestParam(defaultValue = "false") boolean isAsc) {
        Pageable pageable = PageableUtil.createPageableWithSorting(page, size, sortBy, isAsc);
        return ResponseEntity.ok(ApiResponse.success(storeService.searchStores(request.toDto(), pageable)));
    }

    @DeleteMapping("/{storeId}")
    public ResponseEntity<ApiResponse<String>> deleteStore(@PathVariable(name = "storeId") UUID storeId) {
        // todo:
        //  1. 게이트웨이 구현 후 @RequestHeader(name = "X-user-id") String userId 로 수정
        //  2. @RequestHeader(name = "X-user-role") String userRole 추가

        String userId = "testUserId";
        storeService.deleteStore(storeId, userId);
        return ResponseEntity.ok(ApiResponse.success("삭제가 완료되었습니다."));
    }

}
