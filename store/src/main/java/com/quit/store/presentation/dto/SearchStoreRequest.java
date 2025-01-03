package com.quit.store.presentation.dto;

import com.quit.store.application.dto.SearchStoreDto;
import com.quit.store.domain.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SearchStoreRequest {

    private String keyword;
    private Category category;

    public SearchStoreDto toDto() {
        return SearchStoreDto.of(
                this.keyword,
                this.category
        );
    }

}
