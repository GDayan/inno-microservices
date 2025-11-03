package com.innowise.userservice.service;

import com.innowise.userservice.exception.CardValidationException;
import com.innowise.userservice.exception.NotFoundException;
import com.innowise.userservice.mapper.CardInfoMapper;
import com.innowise.userservice.model.dto.CardInfoDto;
import com.innowise.userservice.model.entity.CardInfo;
import com.innowise.userservice.model.entity.User;
import com.innowise.userservice.repository.CardInfoRepository;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.service.impl.CardInfoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardInfoServiceImplTest {
    @Mock
    private CardInfoRepository cardInfoRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardInfoMapper cardInfoMapper;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @InjectMocks
    private CardInfoServiceImpl cardInfoService;

    private User user;
    private CardInfo cardInfo;
    private CardInfoDto cardInfoDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("John");
        user.setSurname("Doe");

        cardInfo = new CardInfo();
        cardInfo.setId(10L);
        cardInfo.setUser(user);
        cardInfo.setHolder("John Doe");
        cardInfo.setNumber("1234 5678 9012 3456");
        cardInfo.setExpirationDate(LocalDate.of(2030, 12, 31));

        cardInfoDto = new CardInfoDto();
        cardInfoDto.setUserId(user.getId());
        cardInfoDto.setHolder("John Doe");
        cardInfoDto.setNumber("1234 5678 9012 3456");
        cardInfoDto.setExpirationDate(LocalDate.of(2030, 12, 31));

        lenient().when(cacheManager.getCache(anyString())).thenReturn(cache);
    }

    @Test
    void saveCard_ShouldSaveSuccessfully() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(cardInfoMapper.cardInfoDtoToCardInfo(cardInfoDto)).thenReturn(cardInfo);
        when(cardInfoRepository.save(cardInfo)).thenReturn(cardInfo);
        when(cardInfoMapper.cardInfoToCardInfoDto(cardInfo)).thenReturn(cardInfoDto);

        CardInfoDto saved = cardInfoService.saveCard(cardInfoDto);

        assertThat(saved.getHolder()).isEqualTo(cardInfoDto.getHolder());
        verify(cardInfoRepository).save(cardInfo);
    }

    @Test
    void saveCard_ShouldThrow_WhenUserNotFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> cardInfoService.saveCard(cardInfoDto));
    }

    @Test
    void saveCard_ShouldThrow_WhenHolderMismatch() {
        cardInfoDto.setHolder("Wrong Name");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        assertThrows(CardValidationException.class, () -> cardInfoService.saveCard(cardInfoDto));
    }

    @Test
    void findCardById_ShouldReturnCard() {
        when(cardInfoRepository.findById(10L)).thenReturn(Optional.of(cardInfo));
        when(cardInfoMapper.cardInfoToCardInfoDto(cardInfo)).thenReturn(cardInfoDto);

        CardInfoDto result = cardInfoService.findCardById(10L);
        assertThat(result.getNumber()).isEqualTo(cardInfoDto.getNumber());
    }

    @Test
    void findCardById_ShouldThrow_WhenNotFound() {
        when(cardInfoRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> cardInfoService.findCardById(10L));
    }

    @Test
    void findCardsByUserId_ShouldReturnCards() {
        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(cardInfoRepository.findByUserId(user.getId())).thenReturn(List.of(cardInfo));
        when(cardInfoMapper.cardInfoToCardInfoDto(cardInfo)).thenReturn(cardInfoDto);

        List<CardInfoDto> result = cardInfoService.findCardsByUserId(user.getId());
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNumber()).isEqualTo(cardInfoDto.getNumber());
    }

    @Test
    void findCardsByUserId_ShouldThrow_WhenUserNotFound() {
        when(userRepository.existsById(user.getId())).thenReturn(false);
        assertThrows(NotFoundException.class, () -> cardInfoService.findCardsByUserId(user.getId()));
    }

    @Test
    void findCardsByIds_ShouldReturnCards() {
        List<Long> ids = List.of(10L);
        when(cardInfoRepository.findByIds(ids)).thenReturn(List.of(cardInfo));
        when(cardInfoMapper.cardInfoToCardInfoDto(cardInfo)).thenReturn(cardInfoDto);

        List<CardInfoDto> result = cardInfoService.findCardsByIds(ids);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNumber()).isEqualTo(cardInfoDto.getNumber());
    }

    @Test
    void updateCard_ShouldUpdateSuccessfully() {
        when(cardInfoRepository.findById(10L)).thenReturn(Optional.of(cardInfo));
        when(cardInfoRepository.save(cardInfo)).thenReturn(cardInfo);
        when(cardInfoMapper.cardInfoToCardInfoDto(cardInfo)).thenReturn(cardInfoDto);

        CardInfoDto updated = cardInfoService.updateCard(10L, cardInfoDto);
        assertThat(updated.getNumber()).isEqualTo(cardInfoDto.getNumber());
    }

    @Test
    void updateCard_ShouldThrow_WhenCardNotFound() {
        when(cardInfoRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> cardInfoService.updateCard(10L, cardInfoDto));
    }

    @Test
    void deleteCard_ShouldDeleteSuccessfully() {
        when(cardInfoRepository.findById(10L)).thenReturn(Optional.of(cardInfo));

        cardInfoService.deleteCard(10L);

        verify(cardInfoRepository).deleteByIdNative(10L);
    }

    @Test
    void deleteCard_ShouldThrow_WhenNotFound() {
        when(cardInfoRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> cardInfoService.deleteCard(10L));
    }

    @Test
    void deleteCardsByUserId_ShouldDeleteAll() {
        Long userId = 1L;

        cardInfoService.deleteCardsByUserId(userId);

        verify(cardInfoRepository, times(1)).deleteAllByUserId(userId);
    }
}
