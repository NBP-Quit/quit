package com.quit.notification.application.service;

import org.springframework.stereotype.Service;

import com.quit.notification.infrastructure.client.UserClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserClient userClient;

	@Override
	public String getSlackEmail(String email) {
		return userClient.getUserByEmail(email).getData().getSlackId();
	}
}
