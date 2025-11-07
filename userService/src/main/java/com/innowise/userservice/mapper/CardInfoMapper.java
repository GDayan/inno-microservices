package com.innowise.userservice.mapper;

import com.innowise.userservice.model.dto.CardInfoDto;
import com.innowise.userservice.model.entity.CardInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CardInfoMapper {
    @Mapping(target = "userId", source = "user.id")
    CardInfoDto cardInfoToCardInfoDto(CardInfo cardInfo);

    @Mapping(target = "user", ignore = true)
    CardInfo cardInfoDtoToCardInfo(CardInfoDto cardInfoDto);

}
