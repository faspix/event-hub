package com.faspix.mapper;

import com.faspix.dto.RequestCompilationDTO;
import com.faspix.dto.ResponseCompilationDTO;
import com.faspix.dto.ResponseCompilationShortDTO;
import com.faspix.entity.Compilation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CompilationMapper {

    ResponseCompilationDTO toResponse(Compilation compilation);

    ResponseCompilationShortDTO toShortResponse(Compilation compilation);

    @Mapping(target = "id", ignore = true)
    Compilation toCompilation(RequestCompilationDTO compilationDTO);

}
