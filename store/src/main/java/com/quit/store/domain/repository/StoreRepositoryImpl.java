package com.quit.store.domain.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.quit.store.application.dto.SearchStoreDto;
import com.quit.store.domain.entity.Category;
import com.quit.store.domain.entity.Store;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.quit.store.domain.entity.QStore.store;

@Slf4j
@RequiredArgsConstructor
public class StoreRepositoryImpl implements StoreRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Store> findAllBySearchRequest(SearchStoreDto request, Pageable pageable) {

        List<Store> storeList = jpaQueryFactory
                .selectFrom(store)
                .where(
                        keywordInNameDescriptionOrAddress(request.getKeyword()),
                        categoryEq(request.getCategory()),
                        store.isDeleted.eq(false)
                )
                .orderBy(store.name.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(store.count())
                .from(store)
                .where(
                        keywordInNameDescriptionOrAddress(request.getKeyword()),
                        categoryEq(request.getCategory()),
                        store.isDeleted.eq(false)
                );

        return PageableExecutionUtils.getPage(storeList, pageable, countQuery::fetchOne);
    }

    private BooleanExpression categoryEq(Category category) {
        return category != null ? store.category.eq(category) : null;
    }

    private BooleanExpression keywordInNameDescriptionOrAddress(String keyword) {
        return keyword != null ? store.name.containsIgnoreCase(keyword)
                .or(store.description.containsIgnoreCase(keyword))
                .or(store.address.containsIgnoreCase(keyword)) : null;
    }

}
