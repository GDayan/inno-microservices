package com.innowise.model.dto;

import com.innowise.model.entity.Card;

import java.time.LocalDate;
import java.util.List;

public class UserDto {
    private Long id;
    private String name;
    private String surname;
    private LocalDate birthDate;
    private String email;
    private List<CardDto> cardDtos;
}
