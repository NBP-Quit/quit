package com.quit.store.application.dto;

import com.quit.store.domain.entity.Category;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchStoreDto {
    private String keyword;
    private Category category;

    @Builder
    private SearchStoreDto(String keyword, Category category) {
        this.keyword = keyword;
        this.category = category;
    }

    public static SearchStoreDto of(String keyword, Category category) {
        return SearchStoreDto.builder()
                .keyword(keyword)
                .category(category)
                .build();
    }

}
