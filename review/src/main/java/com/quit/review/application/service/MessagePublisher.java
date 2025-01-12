package com.quit.review.application.service;

public interface MessagePublisher {
	void publishLikeEvent(LikeEvent likeEvent);
}
