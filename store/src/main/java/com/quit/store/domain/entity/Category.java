package com.quit.store.domain.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Category {

    KOREAN,
    CHINESE,
    WESTERN,
    JAPANESE,
    SOUTHEAST_ASIAN,
    INDIAN,
    FAST_FOOD,
    BUFFET,
    BAKERY,
    DESSERT,
    CAFE
    ;

    @JsonCreator
    public static Category from(String value) {
        for (Category category : Category.values()) {
            if (category.name().equalsIgnoreCase(value)) {
                return category;
            }
        }
        return null;
    }

}
