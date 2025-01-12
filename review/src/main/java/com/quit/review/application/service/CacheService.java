package com.quit.review.application.service;

import java.util.Map;
import java.util.Set;

public interface CacheService {
	Long addToSet(String key, Long value);

	Set<Object> getSetMembers(String key);

	Boolean isMemberOfSet(String key, Long value);

	Long removeFromSet(String key, Long value);

	Long getSetSize(String key);

	Object getHashValue(String key, String hashKey);

	void increaseForHash(String key, String hashKey, long delta);

	void increaseForValue(String key, long delta);

	Object getValue(String key);

	void putAll(String key, Map<String, Object> map);

	Map<Object, Object> getAll(String key);
}
