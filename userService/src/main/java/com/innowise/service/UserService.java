package com.innowise.service;

import com.innowise.mapper.UserMapper;
import com.innowise.model.dto.UserDto;
import com.innowise.model.entity.User;
import com.innowise.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDto create(UserDto userDto){
        User user = userMapper.toEntity(userDto);
        return userMapper.toDto(userRepository.save(user));
    }

    public UserDto getById(Long id){
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new RuntimeException("User not found by ID: " + id));
    }

    public List<UserDto> getUserByIds(List<Long> ids){
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return userMapper.toDtoList(userRepository.findByIds(ids));
    }

    public UserDto getUserByEmail(String email){
        return userRepository.findByEmail(email)
                .map(userMapper::toDto)
                .orElseThrow(() -> new RuntimeException("User not found by email: " + email));
    }

    @Transactional
    public UserDto updateUser(Long id, UserDto userDto){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found by ID: " + id));

        user.setName(userDto.getName());
        user.setSurname(userDto.getSurname());
        user.setEmail(userDto.getEmail());
        user.setBirthDate(userDto.getBirthDate());

        return userMapper.toDto(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id){
        userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found by ID: " + id));
        userRepository.deleteById(id);
    }
}
