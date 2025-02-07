package com.faspix.mapper;

import com.faspix.dto.RequestCategoryDTO;
import com.faspix.dto.ResponseCategoryDTO;
import com.faspix.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    ResponseCategoryDTO categoryToResponse(Category category);

    Category requestToCategory(RequestCategoryDTO categoryDTO);

}
