package com.faspix.mapper;

import com.faspix.client.UserServiceClient;
import com.faspix.dto.ResponseCategoryDTO;
import com.faspix.dto.ResponseUserDTO;
import com.faspix.dto.ResponseUserShortDTO;
import com.faspix.entity.Category;
import com.faspix.entity.Event;
import com.faspix.repository.CategoryRepository;
import com.faspix.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventMapperUtil {

    private final UserServiceClient userServiceClient;

    private final UserMapper userMapper;

    private final CategoryMapper categoryMapper;

    private final CategoryService categoryService;

    @Named("getInitiator")
    public ResponseUserShortDTO getInitiator(Long initiatorId) {
        ResponseUserDTO responseUserDTO = userServiceClient.getUserById(initiatorId);
        return userMapper.responseUserDtoToResponseUserShortDto(responseUserDTO);
    }

    @Named("getCategory")
    public Category getCategory(Long categoryId) {
        return categoryService.findCategoryById(categoryId);
    }

    @Named("getCategoryDTO")
    public ResponseCategoryDTO getCategoryDTO(Category category) {
        return categoryMapper.categoryToResponse(category);
    }

}
