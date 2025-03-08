package com.faspix.mapper;

import com.faspix.dto.RequestCategoryDTO;
import com.faspix.dto.ResponseCategoryDTO;
import com.faspix.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    ResponseCategoryDTO categoryToResponse(Category category);

    @Mapping(target = "categoryId", ignore = true)
    Category requestToCategory(RequestCategoryDTO categoryDTO);

}
