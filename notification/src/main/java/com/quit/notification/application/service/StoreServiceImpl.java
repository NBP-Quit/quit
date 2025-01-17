package com.quit.notification.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.quit.notification.infrastructure.client.StoreClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

	private final StoreClient storeClient;

	@Override
	public String getName(UUID storeId) {
		return storeClient.getStore(storeId).getData().getName();
	}
}
