package com.innowise.userservice.service;

import com.innowise.userservice.model.dto.CardInfoDto;
import java.util.List;

/**
 * Service interface for managing card information.
 * Provides methods for CRUD operations and business logic related to payment cards.
 *
 * @author Dayan Gutlyyev
 * @version 1.0
 * @since 2025
 */
public interface CardInfoService {

    /**
     * Saves a new payment card.
     *
     * @param cardInfoDto the card data transfer object containing card information
     * @return the saved card as DTO
     * @throws com.innowise.userservice.exception.NotFoundException if associated user is not found
     * @throws com.innowise.userservice.exception.CardValidationException if card validation fails
     * @throws IllegalArgumentException if cardInfoDto is null or contains invalid data
     */
    CardInfoDto saveCard(CardInfoDto cardInfoDto);

    /**
     * Finds a card by its unique identifier.
     *
     * @param id the card ID to search for
     * @return the found card as DTO
     * @throws com.innowise.userservice.exception.NotFoundException if card with given ID is not found
     * @throws IllegalArgumentException if id is null or negative
     */
    CardInfoDto findCardById(Long id);

    /**
     * Finds multiple cards by their IDs.
     *
     * @param ids list of card IDs to search for
     * @return list of found cards as DTOs, empty list if no cards found
     * @throws IllegalArgumentException if ids is null or empty
     */
    List<CardInfoDto> findCardsByIds(List<Long> ids);

    /**
     * Finds all cards associated with a specific user.
     *
     * @param userId the user ID to search cards for
     * @return list of user's cards as DTOs, empty list if user has no cards
     * @throws com.innowise.userservice.exception.NotFoundException if user with given ID is not found
     * @throws IllegalArgumentException if userId is null or negative
     */
    List<CardInfoDto> findCardsByUserId(Long userId);

    /**
     * Updates an existing card.
     *
     * @param id the ID of the card to update
     * @param cardInfoDto the card data transfer object with updated information
     * @return the updated card as DTO
     * @throws com.innowise.userservice.exception.NotFoundException if card or associated user is not found
     * @throws com.innowise.userservice.exception.CardValidationException if card validation fails
     * @throws IllegalArgumentException if id is null or cardInfoDto is null
     */
    CardInfoDto updateCard(Long id, CardInfoDto cardInfoDto);

    /**
     * Deletes a card by its ID.
     *
     * @param id the ID of the card to delete
     * @throws com.innowise.userservice.exception.NotFoundException if card with given ID is not found
     * @throws IllegalArgumentException if id is null or negative
     */
    void deleteCard(Long id);

    /**
     * Deletes all cards associated with a specific user.
     *
     * @param userId the user ID whose cards should be deleted
     * @throws com.innowise.userservice.exception.NotFoundException if user with given ID is not found
     * @throws IllegalArgumentException if userId is null or negative
     */
    void deleteCardsByUserId(Long userId);
}