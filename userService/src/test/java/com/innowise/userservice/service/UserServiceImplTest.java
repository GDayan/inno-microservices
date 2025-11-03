package com.innowise.userservice.service;

import com.innowise.userservice.exception.NotFoundException;
import com.innowise.userservice.exception.UserAlreadyExistException;
import com.innowise.userservice.mapper.UserMapper;
import com.innowise.userservice.model.dto.UserDto;
import com.innowise.userservice.model.dto.CardInfoDto;
import com.innowise.userservice.model.dto.UserWithCardsDto;
import com.innowise.userservice.model.entity.User;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private CardInfoService cardInfoService;

    @Mock
    private CacheManager cacheManager;

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private Cache usersCache;

    @Mock
    private Cache emailsCache;

    @Mock
    private Cache usersWithCardsCache;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setName("John");
        user.setSurname("Doe");
        user.setBirthDate(LocalDate.of(1990,1,1));

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("test@example.com");
        userDto.setName("John");
        userDto.setSurname("Doe");
        userDto.setBirthDate(LocalDate.of(1990,1,1));

        lenient().when(cacheManager.getCache("users")).thenReturn(usersCache);
        lenient().when(cacheManager.getCache("emails")).thenReturn(emailsCache);
        lenient().when(cacheManager.getCache("users-with-cards")).thenReturn(usersWithCardsCache);
    }

    // ---------------- save ----------------
    @Test
    void save_ShouldSaveUser_WhenEmailNotExists() {
        when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(false);
        when(userMapper.userDtoToUser(userDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.userToUserDto(user)).thenReturn(userDto);

        UserDto result = userService.save(userDto);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        verify(userRepository).save(user);
    }

    @Test
    void save_ShouldThrowException_WhenEmailExists() {
        when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(true);

        assertThrows(UserAlreadyExistException.class, () -> userService.save(userDto));
    }

    @Test
    void findById_ShouldReturnUser_WhenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.userToUserDto(user)).thenReturn(userDto);

        UserDto result = userService.findById(1L);

        assertEquals(user.getId(), result.getId());
    }

    @Test
    void findById_ShouldThrowNotFound_WhenUserNotExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.findById(1L));
    }

    @Test
    void findByIds_ShouldReturnUsers() {
        List<Long> ids = Arrays.asList(1L, 2L);
        List<User> users = Arrays.asList(user);
        List<UserDto> dtos = Arrays.asList(userDto);

        when(userRepository.findByIds(ids)).thenReturn(users);
        when(userMapper.usersToUsersDto(users)).thenReturn(dtos);

        List<UserDto> result = userService.findByIds(ids);
        assertEquals(1, result.size());
    }

    @Test
    void findUserByEmail_ShouldReturnCachedUser() {
        when(emailsCache.get(user.getEmail(), Long.class)).thenReturn(user.getId());
        when(usersCache.get(user.getId(), UserDto.class)).thenReturn(userDto);

        UserDto result = userService.findUserByEmail(user.getEmail());

        assertEquals(user.getEmail(), result.getEmail());
        verify(userRepository, never()).findByEmail(any());
    }

    @Test
    void findUserByEmail_ShouldFetchFromDb_WhenNotCached() {
        when(emailsCache.get(user.getEmail(), Long.class)).thenReturn(null);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userMapper.userToUserDto(user)).thenReturn(userDto);

        UserDto result = userService.findUserByEmail(user.getEmail());

        assertEquals(user.getEmail(), result.getEmail());
        verify(usersCache).put(user.getId(), userDto);
        verify(emailsCache).put(user.getEmail(), user.getId());
    }

    @Test
    void findUserByEmail_ShouldThrowNotFound_WhenNotExists() {
        when(emailsCache.get(user.getEmail(), Long.class)).thenReturn(null);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.findUserByEmail(user.getEmail()));
    }

    @Test
    void update_ShouldUpdateUser_WhenEmailNotExists() {
        UserDto updateDto = new UserDto();
        updateDto.setEmail("new@example.com");
        updateDto.setName("JohnUpdated");

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setEmail("new@example.com");
        updatedUser.setName("JohnUpdated");

        UserDto updatedDto = new UserDto();
        updatedDto.setId(1L);
        updatedDto.setEmail("new@example.com");
        updatedDto.setName("JohnUpdated");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(updateDto.getEmail())).thenReturn(false);
        doNothing().when(userMapper).updateUserFromUserDto(updateDto, user);
        when(userRepository.save(user)).thenReturn(updatedUser);
        when(userMapper.userToUserDto(updatedUser)).thenReturn(updatedDto);

        UserDto result = userService.update(1L, updateDto);

        assertEquals(updatedDto.getEmail(), result.getEmail());
        assertEquals(updatedDto.getName(), result.getName());
    }

    @Test
    void update_ShouldThrowException_WhenEmailExists() {
        UserDto updateDto = new UserDto();
        updateDto.setEmail("existing@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(updateDto.getEmail())).thenReturn(true);

        assertThrows(UserAlreadyExistException.class, () -> userService.update(1L, updateDto));
    }

    @Test
    void update_ShouldThrowNotFound_WhenUserNotExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.update(1L, userDto));
    }

    @Test
    void deleteByIdNative_ShouldDeleteUser_WhenExists() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(cardInfoService).deleteCardsByUserId(1L);
        doNothing().when(userRepository).deleteByIdNative(1L);

        assertDoesNotThrow(() -> userService.deleteByIdNative(1L));
        verify(userRepository).deleteByIdNative(1L);
    }

    @Test
    void deleteByIdNative_ShouldThrowNotFound_WhenUserNotExists() {
        when(userRepository.existsById(1L)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> userService.deleteByIdNative(1L));
    }


    @Test
    void findUserWithCardsById_ShouldReturnUserWithCards() {
        List<CardInfoDto> cards = List.of(new CardInfoDto());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cardInfoService.findCardsByUserId(1L)).thenReturn(cards);

        UserWithCardsDto result = userService.findUserWithCardsById(1L);

        assertEquals(user.getId(), result.getId());
        assertEquals(cards, result.getCards());
    }

    @Test
    void findUserWithCardsById_ShouldThrowNotFound_WhenUserNotExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.findUserWithCardsById(1L));
    }
}

