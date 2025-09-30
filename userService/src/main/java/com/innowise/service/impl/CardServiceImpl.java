package com.innowise.service.impl;

import com.innowise.exception.NotFoundException;
import com.innowise.mapper.CardMapper;
import com.innowise.model.dto.request.CardRequest;
import com.innowise.model.dto.response.CardResponse;
import com.innowise.model.entity.Card;
import com.innowise.model.entity.User;
import com.innowise.repository.CardRepository;
import com.innowise.repository.UserRepository;
import com.innowise.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardMapper cardMapper;

    @Override
    public CardResponse save(CardRequest cardInfoRequest) {
        User user = userRepository.findById(cardInfoRequest.getUserId())
                .orElseThrow(() -> new NotFoundException("User with id: %s not found".formatted(cardInfoRequest.getUserId())));

        Card card = cardMapper.cardRequestToCard(cardInfoRequest);
        user.addCard(card);

        return cardMapper.cardToCardResponse(cardRepository.save(card));
    }

    @Override
    @Transactional(readOnly = true)
    public CardResponse findById(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("CardInfo with id: %s not found".formatted(id)));

        return cardMapper.cardToCardResponse(card);
    }
    @Override
    @Transactional(readOnly = true)
    public List<CardResponse> findByIds(List<Long> ids) {
        List<Card> cardsInfo = cardRepository.getCardByIds(ids);

        return cardMapper.cardsToCardsResponse(cardsInfo);
    }

    @Override
    public CardResponse updateById(Long id, CardRequest cardInfoRequest) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("CardInfo with id: %s not found".formatted(id)));

        cardMapper.updateCardFromCardRequest(cardInfoRequest, card);

        return cardMapper.cardToCardResponse(cardRepository.save(card));
    }

    @Override
    public void deleteById(Long id) {
        if (!cardRepository.existsById(id)) {
            throw new NotFoundException("CardInfo with id: %s not found".formatted(id));
        }

        cardRepository.deleteById(id);
    }

}
