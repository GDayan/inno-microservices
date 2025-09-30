package com.innowise.mapper;

import com.innowise.model.dto.request.CardRequest;
import com.innowise.model.dto.response.CardResponse;
import com.innowise.model.entity.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CardMapper  {
    Card cardRequestToCard(CardRequest cardRequest);
    void updateCardFromCardRequest(CardRequest cardRequest, @MappingTarget Card card);
    CardResponse cardToCardResponse(Card card);
    List<CardResponse> cardsToCardsResponse(List<Card> cards);
}
