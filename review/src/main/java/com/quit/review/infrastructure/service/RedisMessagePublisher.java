package com.quit.review.infrastructure.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.quit.review.application.service.LikeEvent;
import com.quit.review.application.service.MessagePublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisMessagePublisher implements MessagePublisher {

	private final RedisTemplate<String, Object> redisTemplate;

	@Value("${spring.data.redis.channel.name}")
	private String channelName;

	@Override
	public void publishLikeEvent(LikeEvent likeEvent) {
		log.info("message publish");
		redisTemplate.convertAndSend(channelName, likeEvent);
	}
}
