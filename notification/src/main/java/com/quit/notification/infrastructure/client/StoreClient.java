package com.quit.notification.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.quit.notification.common.ApiResponse;
import com.quit.notification.infrastructure.dto.StoreResponse;

@FeignClient(name = "store")
public interface StoreClient {

	@GetMapping("/api/stores/{storeId}")
	public ApiResponse<StoreResponse> getStore(@PathVariable("storeId") String storeId);
}
