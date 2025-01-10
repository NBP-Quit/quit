package com.quit.reservation.common.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PageUtil {
    public static Pageable adjustPageable(Pageable pageable) {
        List<Integer> allowSize = List.of(10, 30, 50);
        if (!allowSize.contains(pageable.getPageSize())) {
            return PageRequest.of(pageable.getPageNumber(), 10, Sort.Direction.ASC);
        }
        return pageable;
    }
}
