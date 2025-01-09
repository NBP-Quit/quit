package com.quit.review.domain.model;

import org.hibernate.annotations.SQLRestriction;

import com.quit.review.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_like", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"review_id", "user_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("is_deleted is false")
public class Like extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private Review review;

	@Column(nullable = false)
	private Long userId;

	@Builder
	private Like(Review review, Long userId) {
		this.review = review;
		this.userId = userId;
	}

	public static Like create(Review review, Long userId) {
		return Like.builder()
			.review(review)
			.userId(userId)
			.build();
	}
}
