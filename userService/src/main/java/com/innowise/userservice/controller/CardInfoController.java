package com.innowise.userservice.controller;

import com.innowise.userservice.model.dto.CardInfoDto;
import com.innowise.userservice.service.CardInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing payment cards.
 * Provides endpoints for creating, retrieving, updating and deleting card information.
 * All endpoints are prefixed with '/api/v1/cards'.
 *
 * @author Dayan Gutlyyev
 * @version 1.0
 * @since 2025
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users/{userId}/cards")
public class CardInfoController {

    private final CardInfoService cardInfoService;

    @PostMapping
    public ResponseEntity<CardInfoDto> saveCard(
            @PathVariable Long userId,
            @RequestBody CardInfoDto cardInfoDto
    ) {
        cardInfoDto.setUserId(userId);
        CardInfoDto createdCard = cardInfoService.saveCard(cardInfoDto);
        return new ResponseEntity<>(createdCard, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CardInfoDto>> findCardsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(cardInfoService.findCardsByUserId(userId));
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<CardInfoDto> findCardById(
            @PathVariable Long userId,
            @PathVariable Long cardId
    ) {
        CardInfoDto card = cardInfoService.findCardById(cardId);
        return ResponseEntity.ok(card);
    }

    @PutMapping("/{cardId}")
    public ResponseEntity<CardInfoDto> updateCard(
            @PathVariable Long userId,
            @PathVariable Long cardId,
            @RequestBody CardInfoDto cardInfoDto
    ) {
        cardInfoDto.setUserId(userId);
        CardInfoDto updated = cardInfoService.updateCard(cardId, cardInfoDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> deleteCard(
            @PathVariable Long userId,
            @PathVariable Long cardId
    ) {
        cardInfoService.deleteCard(cardId);
        return ResponseEntity.noContent().build();
    }
}