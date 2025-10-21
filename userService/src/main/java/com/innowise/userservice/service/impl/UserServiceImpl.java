package com.innowise.userservice.service.impl;

import com.innowise.userservice.exception.NotFoundException;
import com.innowise.userservice.exception.UserAlreadyExistException;
import com.innowise.userservice.mapper.UserMapper;
import com.innowise.userservice.model.dto.CardInfoDto;
import com.innowise.userservice.model.dto.UserDto;
import com.innowise.userservice.model.dto.UserWithCardsDto;
import com.innowise.userservice.model.entity.User;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.service.CardInfoService;
import com.innowise.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@CacheConfig(cacheNames = "users")
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CardInfoService cardInfoService;

    @Override
    public UserDto save(UserDto userDto) {
        log.info("Saving new user with email: {}", userDto.getEmail());

        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new UserAlreadyExistException(userDto.getEmail());
        }

        User user = userMapper.userDtoToUser(userDto);
        User savedUser = userRepository.save(user);

        log.info("User saved successfully with ID: {}", savedUser.getId());
        return userMapper.userToUserDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto findById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User", id));

        return userMapper.userToUserDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findByIds(List<Long> ids) {

        List<User> users = userRepository.findByIds(ids);
        return userMapper.usersToUsersDto(users);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto findUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User", email));

        return userMapper.userToUserDto(user);
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        log.info("Updating user with ID: {}", id);

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User", id));

        if (!existingUser.getEmail().equals(userDto.getEmail()) &&
                userRepository.existsByEmail(userDto.getEmail())) {
            throw new UserAlreadyExistException(userDto.getEmail());
        }

        userMapper.updateUserFromUserDto(userDto, existingUser);
        User updatedUser = userRepository.save(existingUser);

        log.info("User updated successfully with ID: {}", id);
        return userMapper.userToUserDto(updatedUser);
    }

    @Override
    public void deleteByIdNative(Long id) {
        log.info("Deleting user with ID: {}", id);

        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User", id);
        }

        cardInfoService.deleteCardsByUserId(id);
        userRepository.deleteByIdNative(id);

        log.info("User deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public UserWithCardsDto findUserWithCardsById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User", id));

        List<CardInfoDto> cards = cardInfoService.findCardsByUserId(id);

        return UserWithCardsDto.builder()
                .id(user.getId())
                .name(user.getName())
                .surname(user.getSurname())
                .birthDate(user.getBirthDate())
                .email(user.getEmail())
                .cards(cards)
                .build();
    }
}