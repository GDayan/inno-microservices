package com.innowise.mapper;

import com.innowise.model.dto.UserDto;
import com.innowise.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CardMapper.class})
public interface UserMapper {
    UserDto toDto(User user);
    User toEntity(UserDto userDto);
    List<UserDto> toDtoList(List<User> user);
    List<User> toEntityList(List<UserDto> userDtos);
}
