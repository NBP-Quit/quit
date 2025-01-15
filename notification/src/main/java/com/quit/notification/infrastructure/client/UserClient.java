package com.quit.notification.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.quit.notification.common.ApiResponse;
import com.quit.notification.infrastructure.dto.UserResponse;

@FeignClient(name = "user-service")
public interface UserClient {
	@GetMapping("/api/users/{email}")
	ApiResponse<UserResponse> getUserByEmail(@PathVariable String email);
}
