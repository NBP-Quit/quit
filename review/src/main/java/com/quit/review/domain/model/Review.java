package com.quit.review.domain.model;

import java.util.UUID;

import org.hibernate.annotations.SQLRestriction;

import com.quit.review.common.BaseEntity;

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
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_review")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("is_deleted is false")
public class Review extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false)
	private UUID storeId;

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
}
