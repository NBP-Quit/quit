package com.quit.review.infrastructure.repository;

import static com.quit.review.domain.model.QImage.*;
import static com.quit.review.domain.model.QReview.*;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.quit.review.domain.model.Review;
import com.quit.review.infrastructure.util.QueryDslUtils;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Slice<Review> getSliceByStoreIdAndTags(UUID storeId, Pageable pageable, List<String> tags) {
		List<Review> reviews = queryFactory.selectFrom(review)
			.leftJoin(review.images, image).fetchJoin()
			.where(
				review.storeId.eq(storeId),
				containTag(tags)
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
			.orderBy(QueryDslUtils.getSort(pageable, review))
			.fetch();

		boolean hasNext = reviews.size() > pageable.getPageSize();
		if (hasNext) {
			reviews.remove(reviews.size() - 1);
		}

		return new SliceImpl<>(reviews, pageable, hasNext);
	}

	BooleanExpression containTag(List<String> tags) {
		if (CollectionUtils.isEmpty(tags)) {
			return null;
		}
		BooleanExpression condition = null;
		for (String tag : tags) {
			BooleanExpression tagCondition = review.content.containsIgnoreCase(tag);
			condition = (condition == null) ? tagCondition : condition.or(tagCondition);
		}
		return condition;
	}
}
