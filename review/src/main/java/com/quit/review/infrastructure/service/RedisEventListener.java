package com.quit.review.infrastructure.service;

import java.io.IOException;
import java.util.UUID;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quit.review.application.service.LikeAction;
import com.quit.review.application.service.LikeEvent;
import com.quit.review.common.CustomApiException;
import com.quit.review.domain.model.Like;
import com.quit.review.domain.model.Review;
import com.quit.review.domain.repository.LikeRepository;
import com.quit.review.domain.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisEventListener implements MessageListener {

	private final ReviewRepository reviewRepository;
	private final LikeRepository likeRepository;
	private final ObjectMapper objectMapper;

	@Override
	public void onMessage(Message message, byte[] pattern) {
		try {
			LikeEvent likeEvent = objectMapper.readValue(message.getBody(), LikeEvent.class);
			log.info("message received");

			UUID reviewId = likeEvent.getReviewId();
			Long userId = likeEvent.getUserId();
			LikeAction action = likeEvent.getAction();

			Review review = reviewRepository.findById(reviewId)
				.orElseThrow(() -> new CustomApiException(HttpStatus.NOT_FOUND, "Review not found"));

			switch (action) {
				case LIKE:
					likeRepository.save(Like.create(review, userId));
					break;
				case UNLIKE:
					Like like = likeRepository.findByReviewAndUserId(review, userId)
						.orElseThrow(() -> new CustomApiException(HttpStatus.NOT_FOUND, "Like not found"));
					like.delete();
					break;
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}
}
