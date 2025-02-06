package com.faspix.utility;

import com.faspix.exception.ValidationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageRequestMaker {

    public static Pageable makePageRequest(int page, int size, Sort sort) {
        if (size > 30) {
            throw new ValidationException("Page size cannot be greater then 30");
        }
        return PageRequest.of(page, size, sort);
    }

    public static Pageable makePageRequest(int page, int size) {
        return makePageRequest(page, size, Sort.unsorted());
    }

}
