package com.innowise.userservice.integration;

import com.innowise.userservice.exception.NotFoundException;
import com.innowise.userservice.model.dto.CardInfoDto;
import com.innowise.userservice.model.dto.UserDto;
import com.innowise.userservice.model.dto.UserWithCardsDto;
import com.innowise.userservice.repository.CardInfoRepository;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.service.CardInfoService;
import com.innowise.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.3")
            .withDatabaseName("testdb")
            .withUsername("postgres")
            .withPassword("root");

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7.2.0")
            .withExposedPorts(6379);

    @Autowired
    private UserService userService;

    @Autowired
    private CardInfoService cardInfoService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CardInfoRepository cardInfoRepository;

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");

        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @BeforeEach
    void setup() {
        cardInfoRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void full_crud_and_cache_eviction_flow() {
        UserDto userDto = UserDto.builder()
                .name("Alice")
                .surname("Smith")
                .birthDate(LocalDate.of(1992, 2, 2))
                .email("alice@test.com")
                .build();
        UserDto savedUser = userService.save(userDto);

        CardInfoDto cardDto = CardInfoDto.builder()
                .userId(savedUser.getId())
                .number("1111222233334444")
                .holder("Alice Smith")
                .expirationDate(LocalDate.now().plusYears(1))
                .build();
        CardInfoDto savedCard = cardInfoService.saveCard(cardDto);

        UserWithCardsDto dto1 = userService.findUserWithCardsById(savedUser.getId());
        assertThat(dto1).isNotNull();
        assertThat(dto1.getCards()).hasSize(1);

        UserWithCardsDto dto2 = userService.findUserWithCardsById(savedUser.getId());
        assertThat(dto2).isNotNull();
        assertThat(dto2.getCards()).hasSize(1);

        savedUser.setName("AliceUpdated");
        UserDto updatedUser = userService.update(savedUser.getId(), savedUser);
        assertThat(updatedUser.getName()).isEqualTo("AliceUpdated");

        userService.deleteByIdNative(savedUser.getId());

        assertThatThrownBy(() -> userService.findUserWithCardsById(savedUser.getId()))
                .isInstanceOf(NotFoundException.class);

        List<CardInfoDto> cardsAfterDeletion = cardInfoService.findCardsByUserId(savedUser.getId());
        assertThat(cardsAfterDeletion).isEmpty();
    }
}
