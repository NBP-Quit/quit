package com.quit.notification.infrastructure.service;

import static com.slack.api.model.block.Blocks.*;
import static com.slack.api.model.block.composition.BlockCompositions.*;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.quit.notification.application.service.SlackNotificationService;
import com.quit.notification.infrastructure.messaging.message.ReservationEvent;
import com.quit.notification.infrastructure.messaging.message.ReservationMessage;
import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.methods.response.users.UsersLookupByEmailResponse;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.composition.TextObject;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SlackNotificationServiceImpl implements SlackNotificationService {

	private final Slack slack;

	@Value("${slack.token}")
	private String token;

	@Override
	public String sendDirectMessage
		(String slackEmail, String storeName, ReservationMessage reservationMessage) throws
		SlackApiException,
		IOException {
		String slackId = getSlackIdByEmail(slackEmail);
		return chatPostMessage(slackId, storeName, reservationMessage);
	}

	private String chatPostMessage(String slackId, String storeName, ReservationMessage reservationMessage) throws
		SlackApiException,
		IOException {
		ChatPostMessageResponse response = slack.methods(token).chatPostMessage(request -> request
				.channel(slackId)
				.blocks(createBlocks(storeName, reservationMessage)));
		if (response.isOk()) {
			log.info("Message posted successfully");
			return response.getMessage().getText();
		} else {
			throw new RuntimeException("Failed to send notification to Slack for user " + slackId + " : " + response.getError());
		}
	}

	private String getSlackIdByEmail(String email) throws SlackApiException, IOException {
		UsersLookupByEmailResponse response = slack.methods().usersLookupByEmail(request -> request
				.email(email)
				.token(token));
		if (response.isOk()) {
			log.info("Slack user found: {}", response.getUser().getId());
			return response.getUser().getId();
		} else {
			throw new RuntimeException("Failed to look up user by " + email + " : " + response.getError());
		}
	}

	private List<LayoutBlock> createBlocks(String storeName, ReservationMessage reservationMessage) {
		List<TextObject> reservationInfos = new ArrayList<>();
			reservationInfos.add(markdownText("*예약 ID:*\n" + reservationMessage.getReservationId()));
			reservationInfos.add(markdownText("*예약 일시:*\n"
				+ reservationMessage.getReservationDate().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")) + " "
				+ reservationMessage.getReservationTime().format(DateTimeFormatter.ofPattern("HH시 mm분"))));
			reservationInfos.add(markdownText("*예약 인원:*\n" + reservationMessage.getGuestCount()));
			reservationInfos.add(markdownText("*예약 상태:*\n" + reservationMessage.getReservationEvent().getValue()));
			return asBlocks(
				header(header -> {
					if (reservationMessage.getReservationEvent() == ReservationEvent.CONFIRMED) {
						return header.text(plainText(storeName + " 예약이 확정되었습니다."));
					} else {
						return header.text(plainText(storeName + " 예약이 취소되었습니다."));
					}
				}),
				divider(),
				section(section -> section.text(markdownText("*예약 정보*"))),
				section(section -> section.fields(reservationInfos)),
				divider(),
				section(section -> section.text(markdownText("*결제 금액:* " + reservationMessage.getReservationPrice() + "원")))
			);
	}
}
