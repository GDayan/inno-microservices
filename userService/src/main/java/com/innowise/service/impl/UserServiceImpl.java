package com.innowise.service.impl;

import com.innowise.exception.NotFoundException;
import com.innowise.exception.UserAlreadyExistException;
import com.innowise.mapper.UserMapper;
import com.innowise.model.dto.request.UserRequest;
import com.innowise.model.dto.response.UserResponse;
import com.innowise.model.entity.User;
import com.innowise.repository.UserRepository;
import com.innowise.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    @Override
    public UserResponse save(UserRequest userRequest) {
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new UserAlreadyExistException("User with email: %s already exists".formatted(userRequest.getEmail()));
        }
        User user = userMapper.userRequestToUser(userRequest);

        return userMapper.userToUserResponse(userRepository.save(user));
    }
    @Override
    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id: %s not found".formatted(id)));

        return userMapper.userToUserResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> findByIds(List<Long> ids) {
        List<User> users = userRepository.getByIds(ids);

        return userMapper.usersToUsersResponse(users);
    }
    @Override
    @Transactional(readOnly = true)
    public UserResponse findUserByEmail(String email) {
        User user = userRepository.getByEmail(email)
                .orElseThrow(() -> new NotFoundException("User with email: %s not found".formatted(email)));

        return userMapper.userToUserResponse(user);
    }

    @Override
    public UserResponse updateById(Long id, UserRequest userRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id: %s not found".formatted(id)));
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new UserAlreadyExistException("User with email: %s already exists".formatted(userRequest.getEmail()));
        }

        userMapper.updateUserFromUserRequest(userRequest, user);

        return userMapper.userToUserResponse(userRepository.save(user));
    }

    @Override
    public void deleteById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User with id: %s not found".formatted(id));
        }

        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> findAll(Pageable pageable) {
        List<User> users = userRepository.findAll(pageable).getContent();

        return userMapper.usersToUsersResponse(users);
    }

}
