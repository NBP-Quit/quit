package com.quit.review.infrastructure.util;

import org.springframework.data.domain.Pageable;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.Expressions;

public abstract class QueryDslUtils {
	public static <T> OrderSpecifier<?>[] getSort(Pageable pageable, EntityPathBase<T> qClass) {
		return pageable.getSort().stream().map(order ->
				new OrderSpecifier(
					Order.valueOf(order.getDirection().name()),
					Expressions.path(Object.class, qClass, order.getProperty())
				)).toList()
			.toArray(new OrderSpecifier[0]);
	}

}
