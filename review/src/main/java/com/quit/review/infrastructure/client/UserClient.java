package com.quit.review.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.quit.review.common.ApiResponse;

@FeignClient(name = "user-service")
public interface UserClient {
	@GetMapping("/api/{userId}")
	ApiResponse<UserResponse> getUserById(@PathVariable Long userId);
}
