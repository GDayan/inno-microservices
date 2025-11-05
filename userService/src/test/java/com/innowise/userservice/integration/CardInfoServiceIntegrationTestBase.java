//package com.innowise.userservice.integration;
//
//import com.innowise.userservice.model.dto.CardInfoDto;
//import com.innowise.userservice.model.entity.User;
//import com.innowise.userservice.repository.UserRepository;
//import com.innowise.userservice.service.CardInfoService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//import org.testcontainers.junit.jupiter.Testcontainers;
//import static org.assertj.core.api.Assertions.assertThat;
//
//import java.time.LocalDate;
//import java.util.List;
//
//@SpringBootTest
//@Testcontainers
//class CardInfoServiceIntegrationTestBase extends AbstractIntegrationTestBase {
//    @Autowired
//    private CardInfoService cardInfoService;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Test
//    @Transactional
//    void testSaveAndFindCard() {
//        User user = new User();
//        user.setName("Alice");
//        user.setSurname("Smith");
//        user = userRepository.save(user);
//
//        CardInfoDto dto = new CardInfoDto();
//        dto.setUserId(user.getId());
//        dto.setHolder("Alice Smith");
//        dto.setNumber("1111 2222 3333 4444");
//        dto.setExpirationDate(LocalDate.of(2030, 12, 31));
//
//        CardInfoDto saved = cardInfoService.saveCard(dto);
//        assertThat(saved.getId()).isNotNull();
//        assertThat(saved.getNumber()).isEqualTo(dto.getNumber());
//
//        List<CardInfoDto> cards = cardInfoService.findCardsByUserId(user.getId());
//        assertThat(cards).hasSize(1);
//        assertThat(cards.get(0).getHolder()).isEqualTo("Alice Smith");
//    }
//
//    @Test
//    @Transactional
//    void testDeleteCard() {
//        User user = new User();
//        user.setName("Bob");
//        user.setSurname("Johnson");
//        user = userRepository.save(user);
//
//        CardInfoDto dto = new CardInfoDto();
//        dto.setUserId(user.getId());
//        dto.setHolder("Bob Johnson");
//        dto.setNumber("5555 6666 7777 8888");
//        dto.setExpirationDate(LocalDate.of(2030, 12, 31));
//
//        CardInfoDto saved = cardInfoService.saveCard(dto);
//
//        cardInfoService.deleteCard(saved.getId());
//
//        List<CardInfoDto> cards = cardInfoService.findCardsByUserId(user.getId());
//        assertThat(cards).isEmpty();
//    }
//}
