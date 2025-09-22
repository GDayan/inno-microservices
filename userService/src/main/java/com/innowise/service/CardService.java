package com.innowise.service;

import com.innowise.mapper.CardMapper;
import com.innowise.model.dto.CardDto;
import com.innowise.model.entity.Card;
import com.innowise.repository.CardRepository;
import com.innowise.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardMapper cardMapper;

    public CardDto createCard(CardDto cardDto){
        Card card = cardMapper.toEntity(cardDto);
        card.setUser(userRepository.findById(cardDto.getUserId())
                .orElseThrow(() -> new RuntimeException("Card not found by ID: " + cardDto.getUserId())));
        return cardMapper.toDto(cardRepository.save(card));
    }

    @Transactional
    public CardDto getCardById(Long id){
        return cardRepository.findById(id)
                .map(cardMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Card not found by ID: " + id));
    }

    @Transactional
    public List<CardDto> getCardByIds(List<Long> ids){
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return cardMapper.toDtoList(cardRepository.findCardsByIds(ids));
    }

    public CardDto updateCard(Long id, CardDto cardDto){
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found by ID: " + id));
        card.setNumber(cardDto.getNumber());
        card.setHolder(cardDto.getHolder());
        card.setExpirationDate(cardDto.getExpirationDate());
        card.setUser(userRepository.findById(cardDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found")));
        return cardMapper.toDto(cardRepository.save(card));
    }

    public void deleteCard(Long id){
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found by ID: " + id));
        cardRepository.deleteById(id);
    }
}
