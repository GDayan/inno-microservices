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
@RequestMapping("/api/v1/cards")
public class CardInfoController {

    private final CardInfoService cardInfoService;

    /**
     * Creates a new payment card.
     *
     * @param cardInfoDto the card data to create
     * @return ResponseEntity containing the created card and HTTP status 201 (CREATED)
     * @throws com.innowise.userservice.exception.NotFoundException if associated user is not found
     * @throws com.innowise.userservice.exception.CardValidationException if card validation fails
     */
    @PostMapping
    public ResponseEntity<?> saveCard(@RequestBody CardInfoDto cardInfoDto) {
        try {
            CardInfoDto createdCard = cardInfoService.saveCard(cardInfoDto);
            return new ResponseEntity<>(createdCard, HttpStatus.CREATED);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Retrieves a card by its ID.
     *
     * @param id the ID of the card to retrieve
     * @return ResponseEntity containing the card and HTTP status 200 (OK)
     * @throws com.innowise.userservice.exception.NotFoundException if card with given ID is not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<CardInfoDto> findCardById(@PathVariable("id") Long id) {
        CardInfoDto card = cardInfoService.findCardById(id);
        return ResponseEntity.ok(card);
    }

    /**
     * Retrieves multiple cards by their IDs.
     *
     * @param ids list of card IDs to retrieve
     * @return ResponseEntity containing list of cards and HTTP status 200 (OK)
     */
    @GetMapping
    public ResponseEntity<List<CardInfoDto>> findCardsByIds(@RequestParam("ids") List<Long> ids) {
        List<CardInfoDto> cards = cardInfoService.findCardsByIds(ids);
        return ResponseEntity.ok(cards);
    }

    /**
     * Retrieves all cards associated with a specific user.
     *
     * @param userId the user ID to retrieve cards for
     * @return ResponseEntity containing list of user's cards and HTTP status 200 (OK)
     * @throws com.innowise.userservice.exception.NotFoundException if user with given ID is not found
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CardInfoDto>> findCardsByUserId(@PathVariable("userId") Long userId) {
        List<CardInfoDto> cards = cardInfoService.findCardsByUserId(userId);
        return ResponseEntity.ok(cards);
    }

    /**
     * Updates an existing card.
     *
     * @param id the ID of the card to update
     * @param cardInfoDto the updated card data
     * @return ResponseEntity containing the updated card and HTTP status 200 (OK)
     * @throws com.innowise.userservice.exception.NotFoundException if card or associated user is not found
     * @throws com.innowise.userservice.exception.CardValidationException if card validation fails
     */
    @PutMapping("/{id}")
    public ResponseEntity<CardInfoDto> updateCard(@PathVariable("id") Long id, @RequestBody CardInfoDto cardInfoDto) {
        CardInfoDto updatedCard = cardInfoService.updateCard(id, cardInfoDto);
        return ResponseEntity.ok(updatedCard);
    }

    /**
     * Deletes a card by its ID.
     *
     * @param id the ID of the card to delete
     * @return ResponseEntity with no content and HTTP status 204 (NO_CONTENT)
     * @throws com.innowise.userservice.exception.NotFoundException if card with given ID is not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable("id") Long id) {
        cardInfoService.deleteCard(id);
        return ResponseEntity.noContent().build();
    }
}