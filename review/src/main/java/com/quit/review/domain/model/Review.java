package com.quit.review.domain.model;

import java.time.LocalTime;
import java.util.UUID;

import org.hibernate.annotations.SQLRestriction;
import org.springframework.http.HttpStatus;

import com.quit.review.common.BaseEntity;
import com.quit.review.common.CustomApiException;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "p_review")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("is_deleted is false")
@ToString
public class Review extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false)
	private UUID storeId;

	@Column(nullable = false)
	private UUID reservationId;

	@Column(nullable = false)
	private Long userId;

	@Column(nullable = false)
	private double averageScore;

	@Column(nullable = false)
	@Embedded
	private Scores scores;

	@Column(nullable = false)
	private String nickname;

	@Column(nullable = false, length = 1000)
	private String content;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private MealType mealType;

	@Column(nullable = false)
	private int likeCount = 0;

	@Column(nullable = false)
	private int replyCount = 0;

	@Column(nullable = false)
	private boolean isReported = false;

	@Builder
	private Review(UUID id, UUID storeId, UUID reservationId, Long userId, Scores scores, String nickname,
		String content) {
		this.id = id;
		this.storeId = storeId;
		this.reservationId = reservationId;
		this.userId = userId;
		this.scores = scores;
		this.nickname = nickname;
		this.content = content;
	}

	public void decideMealType(LocalTime reservationTime) {
		if (reservationTime.isAfter(LocalTime.of(5, 59)) && reservationTime.isBefore(LocalTime.of(11, 0))) {
			this.mealType = MealType.BREAKFAST;
		} else if (reservationTime.isAfter(LocalTime.of(10, 59)) && reservationTime.isBefore(LocalTime.of(17, 0))) {
			this.mealType = MealType.LUNCH;
		} else if (reservationTime.isAfter(LocalTime.of(16, 59)) && reservationTime.isBefore(LocalTime.of(23, 59, 59))) {
			this.mealType = MealType.DINNER;
		} else {
			throw new CustomApiException(HttpStatus.BAD_REQUEST, "에약 시간이 지정된 범위에 포함되지 않습니다.");
		}
	}

	public void applyAverageScore() {
		this.averageScore = scores.calculateAverage();
	}
}
