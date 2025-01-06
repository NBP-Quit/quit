package com.quit.review.domain.model;

import java.util.UUID;

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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "p_image")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("is_deleted is false")
public class Image extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	@Setter
	private Review review;

	@Column(nullable = false)
	private String url;

	@Column(nullable = false)
	private String filename;

	@Column(nullable = false)
	private String originalFilename;

	@Column(nullable = false)
	private String contentType;

	@Column(nullable = false)
	private Long size;

	@Builder
	private Image(UUID id, Review review, String url, String filename, String originalFilename, String contentType,
		Long size) {
		this.id = id;
		this.review = review;
		this.url = url;
		this.filename = filename;
		this.originalFilename = originalFilename;
		this.contentType = contentType;
		this.size = size;
	}

	public static Image create(String url, String filename, String originalFilename, String contentType, Long size) {
		return Image.builder()
			.url(url)
			.filename(filename)
			.originalFilename(originalFilename)
			.contentType(contentType)
			.size(size)
			.build();
	}
}
