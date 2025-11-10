package com.innowise.authenticationService.mapper;

import com.innowise.authenticationService.model.dto.UserDto;
import com.innowise.authenticationService.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", source = "password")
    User toEntity(UserDto dto);
}

