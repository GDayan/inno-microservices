package com.innowise.userservice.service;

import com.innowise.userservice.exception.NotFoundException;
import com.innowise.userservice.exception.UserAlreadyExistException;
import com.innowise.userservice.mapper.UserMapper;
import com.innowise.userservice.model.dto.UserDto;
import com.innowise.userservice.model.dto.UserWithCardsDto;
import com.innowise.userservice.model.entity.User;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private CardInfoService cardInfoService;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache emailsCache;

    @Mock
    private Cache usersCache;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(cacheManager.getCache("emails")).thenReturn(emailsCache);
        when(cacheManager.getCache("users")).thenReturn(usersCache);
    }

    private User makeUser(Long id, String email) {
        return User.builder()
                .id(id)
                .name("John")
                .surname("Doe")
                .birthDate(LocalDate.of(1990,1,1))
                .email(email)
                .build();
    }

    private UserDto makeUserDto(Long id, String email) {
        UserDto dto = new UserDto();
        dto.setId(id);
        dto.setName("John");
        dto.setSurname("Doe");
        dto.setBirthDate(LocalDate.of(1990,1,1));
        dto.setEmail(email);
        return dto;
    }

    @Test
    void save_shouldSave_whenEmailNotExists() {
        UserDto dto = makeUserDto(null, "a@b.com");
        User entityToSave = makeUser(null, "a@b.com");
        User saved = makeUser(1L, "a@b.com");
        UserDto savedDto = makeUserDto(1L, "a@b.com");

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(userMapper.userDtoToUser(dto)).thenReturn(entityToSave);
        when(userRepository.save(entityToSave)).thenReturn(saved);
        when(userMapper.userToUserDto(saved)).thenReturn(savedDto);

        UserDto result = userService.save(dto);

        assertThat(result).isEqualTo(savedDto);
        verify(userRepository).save(entityToSave);
    }

    @Test
    void save_shouldThrow_whenEmailExists() {
        UserDto dto = makeUserDto(null, "a@b.com");
        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> userService.save(dto))
                .isInstanceOf(UserAlreadyExistException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void findById_shouldReturnDto_whenExists() {
        User user = makeUser(10L, "x@y.com");
        UserDto dto = makeUserDto(10L, "x@y.com");

        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(userMapper.userToUserDto(user)).thenReturn(dto);

        UserDto res = userService.findById(10L);

        assertThat(res).isEqualTo(dto);
    }

    @Test
    void findById_shouldThrow_whenNotFound() {
        when(userRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(5L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void findUserByEmail_shouldReturnFromCache_whenPresent() {
        // simulate cached id -> then cached userDto
        when(emailsCache.get("a@b.com", Long.class)).thenReturn(1L);
        UserDto cached = makeUserDto(1L, "a@b.com");
        when(usersCache.get(1L, UserDto.class)).thenReturn(cached);

        UserDto res = userService.findUserByEmail("a@b.com");

        assertThat(res).isEqualTo(cached);
        verify(userRepository, never()).findByEmail(any());
    }

    @Test
    void findUserByEmail_shouldLoadAndCache_whenNotCached() {
        when(emailsCache.get("a@b.com", Long.class)).thenReturn(null);

        User user = makeUser(2L, "a@b.com");
        UserDto dto = makeUserDto(2L, "a@b.com");

        when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(user));
        when(userMapper.userToUserDto(user)).thenReturn(dto);

        UserDto res = userService.findUserByEmail("a@b.com");

        assertThat(res).isEqualTo(dto);
        verify(emailsCache).put("a@b.com", 2L);
        verify(usersCache).put(2L, dto);
    }

    @Test
    void update_shouldUpdate_whenOk() {
        Long id = 3L;
        User existing = makeUser(id, "old@a.com");
        UserDto updateDto = makeUserDto(id, "old@a.com");
        when(userRepository.findById(id)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);
        when(userMapper.userToUserDto(existing)).thenReturn(updateDto);

        UserDto res = userService.update(id, updateDto);

        assertThat(res).isEqualTo(updateDto);
        verify(userRepository).save(existing);
    }

    @Test
    void update_shouldThrow_whenNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.update(99L, makeUserDto(99L,"a@b.com"))).isInstanceOf(NotFoundException.class);
    }

    @Test
    void update_shouldThrow_whenEmailConflict() {
        Long id = 4L;
        User existing = makeUser(id, "old@a.com");
        UserDto dto = makeUserDto(id, "new@a.com");

        when(userRepository.findById(id)).thenReturn(Optional.of(existing));
        when(userRepository.existsByEmail("new@a.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.update(id, dto)).isInstanceOf(UserAlreadyExistException.class);
    }

    @Test
    void deleteByIdNative_shouldCallCardServiceAndRepository() {
        Long id = 7L;
        when(userRepository.existsById(id)).thenReturn(true);

        userService.deleteByIdNative(id);

        verify(cardInfoService).deleteCardsByUserId(id);
        verify(userRepository).deleteByIdNative(id);
    }

    @Test
    void deleteByIdNative_shouldThrow_whenNotFound() {
        when(userRepository.existsById(100L)).thenReturn(false);
        assertThatThrownBy(() -> userService.deleteByIdNative(100L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void findUserWithCardsById_shouldReturnDto() {
        Long id = 11L;
        User user = makeUser(id, "z@z.com");
        UserDto userDto = makeUserDto(id, "z@z.com");

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(cardInfoService.findCardsByUserId(id)).thenReturn(List.of());
        UserWithCardsDto expected = UserWithCardsDto.builder()
                .id(id).name("John").surname("Doe").email("z@z.com").cards(List.of()).build();

        UserWithCardsDto res = userService.findUserWithCardsById(id);
        assertThat(res.getId()).isEqualTo(id);
        assertThat(res.getEmail()).isEqualTo("z@z.com");
    }
}
