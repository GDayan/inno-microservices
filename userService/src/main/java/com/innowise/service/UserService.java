package com.innowise.service;

import com.innowise.model.dto.request.UserRequest;
import com.innowise.model.dto.response.UserResponse;
import com.innowise.model.dto.response.UserWithCardResponse;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface UserService extends CrudService<UserRequest, UserResponse, Long> {
    UserResponse findUserByEmail(String email);
    UserResponse findById(Long id);
    List<UserResponse> findAll(Pageable pageable);
}
