package com.innowise.mapper;

import com.innowise.model.dto.request.UserRequest;
import com.innowise.model.dto.response.UserResponse;
import com.innowise.model.dto.response.UserWithCardResponse;
import com.innowise.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User userRequestToUser(UserRequest userRequest);

    void updateUserFromUserRequest(UserRequest userRequest, @MappingTarget User user);

    UserResponse userToUserResponse(User user);

    List<UserResponse> usersToUsersResponse(List<User> users);
}
