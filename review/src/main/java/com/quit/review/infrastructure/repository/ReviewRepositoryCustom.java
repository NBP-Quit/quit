package com.quit.review.infrastructure.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.quit.review.domain.model.MealType;
import com.quit.review.domain.model.Review;
import com.quit.review.domain.model.Tag;

public interface ReviewRepositoryCustom {
	Slice<Review> getSliceByStoreIdAndTags(UUID storeId, Pageable pageable, Tag tag, MealType mealType);
}
