package com.innowise.userservice.service;

import com.innowise.userservice.exception.BadRequestException;
import com.innowise.userservice.exception.NotFoundException;
import com.innowise.userservice.exception.UserAlreadyExistException;
import com.innowise.userservice.mapper.UserMapper;
import com.innowise.userservice.model.dto.CardInfoDto;
import com.innowise.userservice.model.dto.UserDto;
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
        user.setBirthDate(LocalDate.of(1990, 1, 1));

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("test@example.com");
        userDto.setName("John");
        userDto.setSurname("Doe");
        userDto.setBirthDate(LocalDate.of(1990, 1, 1));

        lenient().when(cacheManager.getCache("users")).thenReturn(usersCache);
        lenient().when(cacheManager.getCache("emails")).thenReturn(emailsCache);
        lenient().when(cacheManager.getCache("users-with-cards")).thenReturn(usersWithCardsCache);
    }

    @Test
    void save_ShouldSaveUser_WhenEmailValidAndNotExists() {
        when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(false);
        when(userMapper.userDtoToUser(userDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.userToUserDto(user)).thenReturn(userDto);

        UserDto result = userService.save(userDto);

        assertNotNull(result);
        verify(userRepository).save(user);
    }

    @Test
    void save_ShouldThrowBadRequest_WhenEmailInvalid() {
        userDto.setEmail("invalid-email");
        assertThrows(BadRequestException.class, () -> userService.save(userDto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void save_ShouldThrowUserAlreadyExist_WhenEmailExists() {
        when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(true);
        assertThrows(UserAlreadyExistException.class, () -> userService.save(userDto));
    }

    @Test
    void findById_ShouldReturnUser_WhenExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.userToUserDto(user)).thenReturn(userDto);

        UserDto result = userService.findById(1L);

        assertEquals(userDto.getId(), result.getId());
    }

    @Test
    void findById_ShouldThrowNotFound_WhenNotExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.findById(1L));
    }

    @Test
    void findByIds_ShouldReturnList() {
        List<User> users = List.of(user);
        when(userRepository.findByIds(anyList())).thenReturn(users);
        when(userMapper.usersToUsersDto(users)).thenReturn(List.of(userDto));

        List<UserDto> result = userService.findByIds(List.of(1L));
        assertEquals(1, result.size());
    }

    @Test
    void findUserByEmail_ShouldThrowBadRequest_WhenEmailBlank() {
        assertThrows(BadRequestException.class, () -> userService.findUserByEmail(""));
    }

    @Test
    void findUserByEmail_ShouldReturnFromCache_WhenPresent() {
        when(emailsCache.get(user.getEmail(), Long.class)).thenReturn(1L);
        when(usersCache.get(1L, UserDto.class)).thenReturn(userDto);

        UserDto result = userService.findUserByEmail(user.getEmail());
        assertEquals(user.getEmail(), result.getEmail());
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    void findUserByEmail_ShouldFetchAndCache_WhenNotCached() {
        when(emailsCache.get(user.getEmail(), Long.class)).thenReturn(null);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userMapper.userToUserDto(user)).thenReturn(userDto);

        UserDto result = userService.findUserByEmail(user.getEmail());
        assertEquals(userDto.getEmail(), result.getEmail());
        verify(usersCache).put(1L, userDto);
        verify(emailsCache).put(user.getEmail(), 1L);
    }

    @Test
    void update_ShouldModifyUser_WhenValid() {
        UserDto newDto = new UserDto();
        newDto.setEmail("new@example.com");
        newDto.setName("Updated");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        doNothing().when(userMapper).updateUserFromUserDto(newDto, user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.userToUserDto(user)).thenReturn(newDto);

        UserDto result = userService.update(1L, newDto);
        assertEquals("new@example.com", result.getEmail());
    }

    @Test
    void update_ShouldThrow_WhenEmailAlreadyUsed() {
        UserDto dto = new UserDto();
        dto.setEmail("exists@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("exists@example.com")).thenReturn(true);

        assertThrows(UserAlreadyExistException.class, () -> userService.update(1L, dto));
    }

    @Test
    void update_ShouldThrow_WhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.update(1L, userDto));
    }

    @Test
    void deleteByIdNative_ShouldDelete_WhenUserExists() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(cardInfoService).deleteCardsByUserId(1L);
        doNothing().when(userRepository).deleteByIdNative(1L);

        assertDoesNotThrow(() -> userService.deleteByIdNative(1L));
        verify(cardInfoService).deleteCardsByUserId(1L);
        verify(userRepository).deleteByIdNative(1L);
    }

    @Test
    void deleteByIdNative_ShouldThrow_WhenUserMissing() {
        when(userRepository.existsById(1L)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> userService.deleteByIdNative(1L));
    }

    @Test
    void findUserWithCardsById_ShouldReturnCombinedDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cardInfoService.findCardsByUserId(1L)).thenReturn(List.of(new CardInfoDto()));

        UserWithCardsDto result = userService.findUserWithCardsById(1L);
        assertEquals(1L, result.getId());
        assertEquals(1, result.getCards().size());
    }

    @Test
    void findUserWithCardsById_ShouldThrow_WhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.findUserWithCardsById(1L));
    }
}
