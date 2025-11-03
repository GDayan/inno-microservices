package com.innowise.userservice.service.impl;

import com.innowise.userservice.exception.CardValidationException;
import com.innowise.userservice.exception.NotFoundException;
import com.innowise.userservice.mapper.CardInfoMapper;
import com.innowise.userservice.model.dto.CardInfoDto;
import com.innowise.userservice.model.entity.CardInfo;
import com.innowise.userservice.model.entity.User;
import com.innowise.userservice.repository.CardInfoRepository;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.service.CardInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CardInfoServiceImpl implements CardInfoService {

    private final CardInfoRepository cardInfoRepository;
    private final UserRepository userRepository;
    private final CardInfoMapper cardInfoMapper;

    @Override
    @Caching(evict = {
            @CacheEvict(value = "cardInfo", allEntries = true),
            @CacheEvict(value = "cardsByUser", key = "#cardInfoDto.userId")
    })
    public CardInfoDto saveCard(CardInfoDto cardInfoDto) {
        User user = userRepository.findById(cardInfoDto.getUserId())
                .orElseThrow(() -> new NotFoundException("User", cardInfoDto.getUserId()));

        validateCardHolder(user, cardInfoDto.getHolder());

        CardInfo cardInfo = cardInfoMapper.cardInfoDtoToCardInfo(cardInfoDto);
        cardInfo.setUser(user);

        CardInfo savedCard = cardInfoRepository.save(cardInfo);
        return cardInfoMapper.cardInfoToCardInfoDto(savedCard);
    }

    @Override
    @Cacheable(value = "cardInfo", key = "#id", condition = "#id != null")
    @Transactional(readOnly = true)
    public CardInfoDto findCardById(Long id) {
        log.info("Fetching card ID {} from DB (cache miss)", id);
        return cardInfoRepository.findById(id)
                .map(cardInfoMapper::cardInfoToCardInfoDto)
                .orElseThrow(() -> new NotFoundException("Card", id));
    }

    @Override
    @Cacheable(value = "cardsByUser", key = "#userId", condition = "#userId != null")
    @Transactional(readOnly = true)
    public List<CardInfoDto> findCardsByUserId(Long userId) {
        log.info("Fetching cards for user ID {} from DB (cache miss)", userId);

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User", userId);
        }

        List<CardInfo> cards = cardInfoRepository.findByUserId(userId);
        return cards.stream()
                .map(cardInfoMapper::cardInfoToCardInfoDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CardInfoDto> findCardsByIds(List<Long> ids) {
        return cardInfoRepository.findByIds(ids).stream()
                .map(cardInfoMapper::cardInfoToCardInfoDto)
                .collect(Collectors.toList());
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "cardInfo", key = "#id"),
            @CacheEvict(value = "cardsByUser", key = "#cardInfoDto.userId")
    })
    public CardInfoDto updateCard(Long id, CardInfoDto cardInfoDto) {
        CardInfo existingCard = cardInfoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Card", id));

        Long originalUserId = existingCard.getUser().getId();
        Long newUserId = cardInfoDto.getUserId();

        if (newUserId != null && !originalUserId.equals(newUserId)) {
            User user = userRepository.findById(newUserId)
                    .orElseThrow(() -> new NotFoundException("User", newUserId));
            existingCard.setUser(user);

            if (cardInfoDto.getHolder() != null) {
                validateCardHolder(user, cardInfoDto.getHolder());
            }
        } else if (cardInfoDto.getHolder() != null) {
            validateCardHolder(existingCard.getUser(), cardInfoDto.getHolder());
        }

        if (cardInfoDto.getNumber() != null) existingCard.setNumber(cardInfoDto.getNumber());
        if (cardInfoDto.getHolder() != null) existingCard.setHolder(cardInfoDto.getHolder());
        if (cardInfoDto.getExpirationDate() != null) existingCard.setExpirationDate(cardInfoDto.getExpirationDate());

        CardInfo updatedCard = cardInfoRepository.save(existingCard);
        return cardInfoMapper.cardInfoToCardInfoDto(updatedCard);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "cardInfo", key = "#id"),
            @CacheEvict(value = "cardsByUser", allEntries = true)
    })
    @Transactional
    public void deleteCard(Long id) {
        CardInfo card = cardInfoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Card", id));
        cardInfoRepository.deleteByIdNative(id);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "cardsByUser", key = "#userId"),
            @CacheEvict(value = "cardInfo", allEntries = true)
    })
    public void deleteCardsByUserId(Long userId) {
        log.info("Deleting cards for user ID: {}", userId);
        cardInfoRepository.deleteAllByUserId(userId);
        log.info("Cards deleted successfully for user ID: {}", userId);
    }

    private void validateCardHolder(User user, String cardHolder) {
        String expectedHolder = user.getName() + " " + user.getSurname();
        String normalizedCardHolder = cardHolder.trim();
        String normalizedExpectedHolder = expectedHolder.trim();

        if (!normalizedCardHolder.equalsIgnoreCase(normalizedExpectedHolder)) {
            throw new CardValidationException(
                    String.format("Card holder name '%s' does not match user name '%s %s'. Expected: '%s'",
                            cardHolder, user.getName(), user.getSurname(), expectedHolder)
            );
        }
    }
}
