package com.innowise.userservice.service.impl;

import com.innowise.userservice.exception.CardValidationException;
import com.innowise.userservice.exception.NotFoundException;
import com.innowise.userservice.mapper.CardInfoMapper;
import com.innowise.userservice.model.dto.CardInfoDto;
import com.innowise.userservice.model.entity.CardInfo;
import com.innowise.userservice.model.entity.User;
import com.innowise.userservice.repository.CardInfoRepository;
import com.innowise.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardInfoServiceImplTest {

    @Mock
    private CardInfoRepository cardInfoRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardInfoMapper cardInfoMapper;

    @InjectMocks
    private CardInfoServiceImpl cardInfoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private User makeUser(Long id) {
        return User.builder().id(id).name("John").surname("Doe").email("a@b.com").birthDate(LocalDate.of(1990,1,1)).build();
    }

    private CardInfo makeCard(Long id, User user) {
        CardInfo c = new CardInfo();
        c.setId(id);
        c.setUser(user);
        c.setNumber("1111222233334444");
        c.setHolder(user.getName() + " " + user.getSurname());
        c.setExpirationDate(LocalDate.now().plusYears(1));
        return c;
    }

    private CardInfoDto makeCardDto(Long id, Long userId) {
        CardInfoDto dto = new CardInfoDto();
        dto.setId(id);
        dto.setUserId(userId);
        dto.setNumber("1111222233334444");
        dto.setHolder("John Doe");
        dto.setExpirationDate(LocalDate.now().plusYears(1));
        return dto;
    }

    @Test
    void saveCard_shouldSave_whenValid() {
        User user = makeUser(1L);
        CardInfoDto dto = makeCardDto(null, 1L);
        CardInfo entity = makeCard(null, user);
        CardInfo saved = makeCard(5L, user);
        CardInfoDto savedDto = makeCardDto(5L, 1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cardInfoMapper.cardInfoDtoToCardInfo(dto)).thenReturn(entity);
        when(cardInfoRepository.save(entity)).thenReturn(saved);
        when(cardInfoMapper.cardInfoToCardInfoDto(saved)).thenReturn(savedDto);

        CardInfoDto res = cardInfoService.saveCard(dto);

        assertThat(res).isEqualTo(savedDto);
    }

    @Test
    void saveCard_shouldThrow_whenUserNotFound() {
        CardInfoDto dto = makeCardDto(null, 99L);
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> cardInfoService.saveCard(dto)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void saveCard_shouldThrow_whenHolderMismatch() {
        User user = makeUser(2L);
        CardInfoDto dto = makeCardDto(null, 2L);
        dto.setHolder("Someone Else");

        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(cardInfoMapper.cardInfoDtoToCardInfo(dto)).thenReturn(makeCard(null, user));

        assertThatThrownBy(() -> cardInfoService.saveCard(dto)).isInstanceOf(CardValidationException.class);
    }

    @Test
    void findCardById_shouldReturnDto_whenFound() {
        User user = makeUser(3L);
        CardInfo card = makeCard(3L, user);
        CardInfoDto dto = makeCardDto(3L, 3L);

        when(cardInfoRepository.findById(3L)).thenReturn(Optional.of(card));
        when(cardInfoMapper.cardInfoToCardInfoDto(card)).thenReturn(dto);

        CardInfoDto res = cardInfoService.findCardById(3L);
        assertThat(res).isEqualTo(dto);
    }

    @Test
    void findCardById_shouldThrow_whenNotFound() {
        when(cardInfoRepository.findById(50L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> cardInfoService.findCardById(50L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void findCardsByUserId_shouldReturnList_whenUserExists() {
        User user = makeUser(4L);
        CardInfo card = makeCard(4L, user);
        CardInfoDto dto = makeCardDto(4L, 4L);

        when(userRepository.existsById(4L)).thenReturn(true);
        when(cardInfoRepository.findByUserId(4L)).thenReturn(List.of(card));
        when(cardInfoMapper.cardInfoToCardInfoDto(card)).thenReturn(dto);

        var list = cardInfoService.findCardsByUserId(4L);
        assertThat(list).hasSize(1).contains(dto);
    }

    @Test
    void findCardsByUserId_shouldThrow_whenUserNotExists() {
        when(userRepository.existsById(99L)).thenReturn(false);
        assertThatThrownBy(() -> cardInfoService.findCardsByUserId(99L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void updateCard_shouldUpdate_holderAndNumber() {
        User user = makeUser(6L);
        CardInfo existing = makeCard(6L, user);
        CardInfoDto dto = makeCardDto(6L, 6L);
        dto.setHolder("John Doe");
        dto.setNumber("9999888877776666");

        when(cardInfoRepository.findById(6L)).thenReturn(Optional.of(existing));
        when(cardInfoRepository.save(existing)).thenReturn(existing);
        when(cardInfoMapper.cardInfoToCardInfoDto(existing)).thenReturn(dto);

        var updated = cardInfoService.updateCard(6L, dto);
        assertThat(updated.getNumber()).isEqualTo("9999888877776666");
    }

    @Test
    void updateCard_shouldThrow_whenCardNotFound() {
        when(cardInfoRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> cardInfoService.updateCard(999L, makeCardDto(999L, 1L))).isInstanceOf(NotFoundException.class);
    }

    @Test
    void deleteCard_shouldCallRepository_whenExists() {
        User user = makeUser(7L);
        CardInfo card = makeCard(7L, user);
        when(cardInfoRepository.findById(7L)).thenReturn(Optional.of(card));

        cardInfoService.deleteCard(7L);

        verify(cardInfoRepository).deleteByIdNative(7L);
    }

    @Test
    void deleteCardsByUserId_shouldCallRepository() {
        cardInfoService.deleteCardsByUserId(8L);
        verify(cardInfoRepository).deleteAllByUserId(8L);
    }
}
