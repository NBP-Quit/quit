package com.quit.notification.infrastructure.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.quit.notification.common.ApiResponse;
import com.quit.notification.infrastructure.dto.StoreResponse;

@FeignClient(name = "store")
public interface StoreClient {

	@GetMapping("/api/stores/{storeId}")
	ApiResponse<StoreResponse> getStore(@PathVariable("storeId") UUID storeId);
}
