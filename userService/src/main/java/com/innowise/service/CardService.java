package com.innowise.service;

import com.innowise.model.entity.Card;
import com.innowise.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;

    public Card createCard(Card card){
        return cardRepository.save(card);
    }

    public Optional<Card> getCardById(Long id){
        return cardRepository.findById(id);
    }

    public List<Card> getCardByIds(List<Long> ids){
        return cardRepository.findCardsByIds(ids);
    }

    public Card updateCard(Long id, Card update){
        return cardRepository.findById(id).map(card -> {
            card.setNumber(update.getNumber());
            card.setHolder(update.getHolder());
            card.setExpirationDate(update.getExpirationDate());
            card.setUser(update.getUser());
            return cardRepository.save(card);
        }).orElseThrow(() -> new RuntimeException("Card not found"));
    }

    public void deleteCard(Long id){
        cardRepository.deleteById(id);
    }
}
