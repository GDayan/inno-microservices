package com.innowise.userservice.mapper;

import com.innowise.userservice.model.dto.UserDto;
import com.innowise.userservice.model.entity.User;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CardInfoMapper.class})
public interface UserMapper {

    User userDtoToUser(UserDto userDto);

    @Mapping(target = "cardInfos", source = "cardInfos")
    UserDto userToUserDto(User user);

    List<UserDto> usersToUsersDto(List<User> users);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromUserDto(UserDto userDto, @MappingTarget User user);
}
