//package com.innowise.userservice.integration;
//
//import com.innowise.userservice.model.dto.CardInfoDto;
//import com.innowise.userservice.model.entity.User;
//import com.innowise.userservice.repository.UserRepository;
//import com.innowise.userservice.service.CardInfoService;
//import com.innowise.userservice.service.UserService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.cache.CacheManager;
//import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//
//@EnableCaching
//@ActiveProfiles("test")
//@Transactional
//class UserAndCardIntegrationTest extends AbstractIntegrationTestBase {
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private CardInfoService cardInfoService;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private CacheManager cacheManager;
//
//    private User user;
//
//    @BeforeEach
//    void setUp() {
//        user = User.builder()
//                .name("John")
//                .surname("Doe")
//                .email("john.doe@example.com")
//                .build();
//
//        user = userRepository.save(user);
//
//        // Очищаем кэши перед каждым тестом
//        cacheManager.getCache("cardsByUser").clear();
//        cacheManager.getCache("cardInfo").clear();
//    }
//
//    @Test
//    void createUserAndCard_ShouldPersistAndCache() {
//        CardInfoDto cardDto = new CardInfoDto();
//        cardDto.setUserId(user.getId());
//        cardDto.setHolder("John Doe");
//        cardDto.setNumber("1234 5678 9012 3456");
//        cardDto.setExpirationDate(LocalDate.now().plusYears(3));
//
//        CardInfoDto savedCard = cardInfoService.saveCard(cardDto);
//
//        CardInfoDto fromDb = cardInfoService.findCardById(savedCard.getId());
//        assertThat(fromDb).isNotNull();
//        assertThat(fromDb.getHolder()).isEqualTo("John Doe");
//
//        CardInfoDto cachedCard = Optional.ofNullable(cacheManager.getCache("cardInfo"))
//                .map(c -> c.get(savedCard.getId()))
//                .map(v -> (CardInfoDto) v.get())
//                .orElseThrow(() -> new AssertionError("Card not found in cache!"));
//
//        assertThat(cachedCard).isNotNull();
//        assertThat(cachedCard.getHolder()).isEqualTo("John Doe");
//    }
//
//    @Test
//    void findCardsByUser_ShouldReturnCardsFromDbAndCache() {
//        CardInfoDto card1 = new CardInfoDto();
//        card1.setUserId(user.getId());
//        card1.setHolder("John Doe");
//        card1.setNumber("1111 2222 3333 4444");
//        card1.setExpirationDate(LocalDate.now().plusYears(2));
//        cardInfoService.saveCard(card1);
//
//        CardInfoDto card2 = new CardInfoDto();
//        card2.setUserId(user.getId());
//        card2.setHolder("John Doe");
//        card2.setNumber("5555 6666 7777 8888");
//        card2.setExpirationDate(LocalDate.now().plusYears(3));
//        cardInfoService.saveCard(card2);
//
//        List<CardInfoDto> cards = cardInfoService.findCardsByUserId(user.getId());
//        assertThat(cards).hasSize(2);
//
//        List<CardInfoDto> cachedCards = Optional.ofNullable(cacheManager.getCache("cardsByUser"))
//                .map(c -> c.get(user.getId()))
//                .map(v -> (List<CardInfoDto>) v.get())
//                .orElseThrow(() -> new AssertionError("Cards not found in cache!"));
//
//        assertThat(cachedCards).hasSize(2);
//    }
//
//
//    @Test
//    void deleteUser_ShouldCascadeCards() {
//        // --- Сохраняем карту ---
//        CardInfoDto cardDto = new CardInfoDto();
//        cardDto.setUserId(user.getId());
//        cardDto.setHolder("John Doe");
//        cardDto.setNumber("9999 0000 1111 2222");
//        cardDto.setExpirationDate(LocalDate.now().plusYears(1));
//        CardInfoDto savedCard = cardInfoService.saveCard(cardDto);
//
//        // --- Удаляем пользователя ---
//        userService.deleteByIdNative(user.getId());
//
//        // --- Проверяем, что пользователь удалён ---
//        assertThat(userRepository.findById(user.getId())).isEmpty();
//
//        // --- Проверяем, что карта удалена ---
//        assertThatThrownBy(() -> cardInfoService.findCardById(savedCard.getId()))
//                .isInstanceOf(com.innowise.userservice.exception.NotFoundException.class);
//    }
//}