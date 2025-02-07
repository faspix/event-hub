package com.faspix.mapper;

import com.faspix.dto.RequestCompilationDTO;
import com.faspix.dto.ResponseCompilationDTO;
import com.faspix.entity.Compilation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CompilationMapperUtil.class})
public interface CompilationMapper {

    @Mapping(target = "events", source = "events", qualifiedByName = "getEventsDto")
    ResponseCompilationDTO compilationToResponse(Compilation compilation);

    Compilation requestToCompilation(RequestCompilationDTO compilationDTO);

}
