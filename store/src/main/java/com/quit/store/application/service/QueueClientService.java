package com.quit.store.application.service;

import java.util.UUID;

public interface QueueClientService {
    void assignStoreToServer(UUID storeId, String userRole);
}
