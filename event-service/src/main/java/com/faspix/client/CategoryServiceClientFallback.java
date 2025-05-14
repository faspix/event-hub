package com.faspix.client;

import com.faspix.shared.dto.ResponseCategoryDTO;
import com.faspix.shared.exception.ServiceUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CategoryServiceClientFallback implements CategoryServiceClient {
    @Override
    public ResponseCategoryDTO getCategoryById(Long categoryId) {
        log.error("Error during calling category service for getCategoryById, category id: {} ", categoryId);
        throw new ServiceUnavailableException("Error during calling category service");
    }
}
