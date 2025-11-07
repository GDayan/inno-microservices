package com.innowise.userservice.service.impl;

import com.innowise.userservice.exception.BadRequestException;
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
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
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
    private final CacheManager cacheManager;


    @Override
    public UserDto save(UserDto userDto) {
        log.info("Saving new user with email: {}", userDto.getEmail());

        if (!userDto.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw BadRequestException.invalidEmail(userDto.getEmail());
        }

        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new UserAlreadyExistException("User already exists", userDto.getEmail());
        }
        User user = userMapper.userDtoToUser(userDto);
        User savedUser = userRepository.save(user);

        log.info("User saved successfully with ID: {}", savedUser.getId());
        return userMapper.userToUserDto(savedUser);
    }

    @Cacheable(cacheNames = "users", key = "#id", condition = "#id != null")
    @Override
    @Transactional(readOnly = true)
    public UserDto findById(Long id) {
        log.info("CACHE KEY id = {}", id);
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
        if (email == null || email.isBlank()) {
            throw BadRequestException.missingParameter("email");
        }
        Long id = cacheManager.getCache("emails").get(email, Long.class);
        if (id != null) {
            UserDto cachedUser = cacheManager.getCache("users").get(id, UserDto.class);
            if (cachedUser != null) {
                return cachedUser;
            }
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User", email));

        UserDto userDto = userMapper.userToUserDto(user);

        cacheManager.getCache("emails").put(email, user.getId());
        cacheManager.getCache("users").put(user.getId(), userDto);

        return userDto;
    }

    @CacheEvict(cacheNames = "users", key = "#id", condition = "#id != null")
    @Override
    public UserDto update(Long id, UserDto userDto) {
        log.info("Updating user with ID: {}", id);

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User", id));

        if (!existingUser.getEmail().equals(userDto.getEmail()) &&
                userRepository.existsByEmail(userDto.getEmail())) {
            throw new UserAlreadyExistException("User already exists", userDto.getEmail());
        }

        userMapper.updateUserFromUserDto(userDto, existingUser);
        User updatedUser = userRepository.save(existingUser);

        log.info("User updated successfully with ID: {}", id);
        return userMapper.userToUserDto(updatedUser);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "users", key = "#id", condition = "#id != null"),
            @CacheEvict(cacheNames = "users-with-cards", key = "#id", condition = "#id != null"),
            @CacheEvict(cacheNames = "emails", allEntries = true)
    })
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


    @Cacheable(cacheNames = "users-with-cards", key = "#id", condition = "#id != null")
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