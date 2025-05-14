package com.faspix.mapper;

import com.faspix.entity.Category;
import com.faspix.shared.dto.RequestCategoryDTO;
import com.faspix.shared.dto.ResponseCategoryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    ResponseCategoryDTO categoryToResponse(Category category);

    @Mapping(target = "categoryId", ignore = true)
    Category requestToCategory(RequestCategoryDTO categoryDTO);

}
