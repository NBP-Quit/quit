package com.quit.review.infrastructure.repository;

import static com.quit.review.domain.model.QImage.*;
import static com.quit.review.domain.model.QReview.*;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.quit.review.domain.model.MealType;
import com.quit.review.domain.model.Review;
import com.quit.review.domain.model.Tag;
import com.quit.review.infrastructure.util.QueryDslUtils;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Slice<Review> getSliceByStoreIdAndTags(UUID storeId, Pageable pageable, Tag tag, MealType mealType) {
		List<Review> reviews = queryFactory.selectFrom(review)
			.leftJoin(review.images, image).fetchJoin()
			.where(
				review.storeId.eq(storeId),
				review.isDeleted.eq(false),
				mealTypeEq(mealType),
				contentContains(tag)
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

	BooleanExpression contentContains(Tag tag) {
		return tag != null ? review.content.containsIgnoreCase(tag.getValue()) : null;
	}

	BooleanExpression mealTypeEq(MealType mealType) {
		return mealType != null ? review.mealType.eq(mealType) : null;
	}
}
