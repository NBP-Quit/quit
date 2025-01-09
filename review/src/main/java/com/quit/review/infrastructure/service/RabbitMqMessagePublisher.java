package com.quit.review.infrastructure.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.quit.review.application.service.LikeEvent;
import com.quit.review.application.service.MessagePublisher;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RabbitMqMessagePublisher implements MessagePublisher {

	private final RabbitTemplate rabbitTemplate;

	@Value("${message.exchange}")
	private String exchange;

	@Value("${message.routing-key}")
	private String routingKey;

	@Override
	public void publishLikeEvent(LikeEvent likeEvent) {
		rabbitTemplate.convertAndSend(exchange, routingKey, likeEvent);
	}
}
