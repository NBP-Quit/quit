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
import org.apache.zookeeper.proto.RequestHeader;
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
    public ResponseEntity<ApiResponse<CreateStoreResponse>> createStore(
            @RequestHeader(name = "X-User-Email") String userId,
            @RequestHeader(name = "X-User-Role") String userRole,
            @Valid @RequestBody CreateStoreRequest request) {
        CreateStoreResponse response = storeService.createStore(request.toDto(), userId, userRole);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{storeId}")
    public ResponseEntity<ApiResponse<StoreResponse>> updateStore(
            @RequestHeader(name = "X-User-Email") String userId,
            @RequestHeader(name = "X-User-Role") String userRole,
            @PathVariable(name = "storeId") UUID storeId,
            @RequestBody UpdateStoreRequest request) {
        StoreResponse response = storeService.updateStore(storeId, request.toDto(), userId, userRole);
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
    public ResponseEntity<ApiResponse<String>> deleteStore(
            @RequestHeader(name = "X-User-Email") String userId,
            @RequestHeader(name = "X-User-Role") String userRole,
            @PathVariable(name = "storeId") UUID storeId) {
        storeService.deleteStore(storeId, userId, userRole);
        return ResponseEntity.ok(ApiResponse.success("삭제가 완료되었습니다."));
    }

    @GetMapping("/{storeId}/internal")
    public ResponseEntity<ApiResponse<Boolean>> getStoreForInternal(@PathVariable(name = "storeId") UUID storeId) {
        return ResponseEntity.ok(ApiResponse.success(storeService.getStoreForInternal(storeId)));
    }

}
