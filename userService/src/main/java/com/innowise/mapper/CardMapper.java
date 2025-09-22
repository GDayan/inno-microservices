package com.innowise.mapper;

import com.innowise.model.dto.CardDto;
import com.innowise.model.entity.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CardMapper  {
    @Mapping(source = "user.id", target = "userId")
    CardDto toDto(Card card);

    @Mapping(source = "userId", target = "user.id")
    Card toEntity(CardDto cardDto);

    List<CardDto> toDtoList(List<Card> cards);
    List<Card> toEntityList(List<CardDto> cardDtos);
}
