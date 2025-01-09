package com.quit.review.application.service;

import java.util.Set;

public interface CacheService {
	Long addToSet(String key, Object values);

	Set<Object> getSetMembers(String key);

	Boolean isMemberOfSet(String key, Object value);

	Long removeFromSet(String key, Object values);

	Long getSetSize(String key);
}
