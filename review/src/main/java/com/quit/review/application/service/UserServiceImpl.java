package com.quit.review.application.service;

import org.springframework.stereotype.Service;

import com.quit.review.common.ApiResponse;
import com.quit.review.infrastructure.client.UserClient;
import com.quit.review.infrastructure.client.UserResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

	private final UserClient userClient;

	@Override
	public String getNicknameById(Long userId) {
		ApiResponse<UserResponse> response = userClient.getUserById(userId);
		if (response.getCode() != 200) {
			throw new NullPointerException("User not found");
		}
		return response.getData().getNickname();
	}
}
