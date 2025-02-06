package com.faspix.mapper;

import com.faspix.dto.ResponseUserDTO;
import com.faspix.dto.ResponseUserShortDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    ResponseUserShortDTO responseUserDtoToResponseUserShortDto(ResponseUserDTO userDTO);

}
