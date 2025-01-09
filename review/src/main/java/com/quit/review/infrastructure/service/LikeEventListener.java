package com.quit.review.infrastructure.service;

import java.util.UUID;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.quit.review.application.service.LikeAction;
import com.quit.review.application.service.LikeEvent;
import com.quit.review.common.CustomApiException;
import com.quit.review.domain.model.Like;
import com.quit.review.domain.model.Review;
import com.quit.review.domain.repository.LikeRepository;
import com.quit.review.domain.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LikeEventListener {

	private final ReviewRepository reviewRepository;
	private final LikeRepository likeRepository;

	@RabbitListener(queues = "${message.queue}")
	@Retryable(
		retryFor = { Exception.class },
		maxAttempts = 5,
		backoff = @Backoff(delay = 5000)
	)
	@Transactional
	public void handleLikeEvent(LikeEvent likeEvent) {
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
	}
}
