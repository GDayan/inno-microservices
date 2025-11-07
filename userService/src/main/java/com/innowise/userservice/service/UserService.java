package com.innowise.userservice.service;

import com.innowise.userservice.model.dto.UserDto;
import com.innowise.userservice.model.dto.UserWithCardsDto;

import java.util.List;

/**
 * Service interface for managing users.
 * Provides methods for CRUD operations and business logic related to users.
 *
 * @author Dayan Gutlyyev
 * @version 1.0
 * @since 2025
 */
public interface UserService {

    /**
     * Saves a new user.
     *
     * @param userDto the user data transfer object containing user information
     * @return the saved user as DTO
     * @throws IllegalArgumentException if userDto is null or contains invalid data
     * @throws com.innowise.userservice.exception.UserAlreadyExistException if email already exists
     */
    UserDto save(UserDto userDto);

    /**
     * Finds a user by their unique identifier.
     *
     * @param id the user ID to search for
     * @return the found user as DTO
     * @throws com.innowise.userservice.exception.NotFoundException if user with given ID is not found
     * @throws IllegalArgumentException if id is null or negative
     */
    UserDto findById(Long id);

    /**
     * Finds multiple users by their IDs.
     *
     * @param ids list of user IDs to search for
     * @return list of found users as DTOs, empty list if no users found
     * @throws IllegalArgumentException if ids is null or empty
     */
    List<UserDto> findByIds(List<Long> ids);

    /**
     * Finds a user by their email address.
     *
     * @param email the email address to search for
     * @return the found user as DTO
     * @throws com.innowise.userservice.exception.NotFoundException if user with given email is not found
     * @throws IllegalArgumentException if email is null or empty
     */
    UserDto findUserByEmail(String email);

    /**
     * Updates an existing user.
     *
     * @param id the ID of the user to update
     * @param userDto the user data transfer object with updated information
     * @return the updated user as DTO
     * @throws com.innowise.userservice.exception.NotFoundException if user with given ID is not found
     * @throws IllegalArgumentException if id is null or userDto is null
     */
    UserDto update(Long id, UserDto userDto);

    /**
     * Deletes a user by their ID, including all associated cards.
     * Uses native SQL query for deletion.
     *
     * @param id the ID of the user to delete
     * @throws com.innowise.userservice.exception.NotFoundException if user with given ID is not found
     * @throws IllegalArgumentException if id is null or negative
     */
    void deleteByIdNative(Long id);

    /**
     * Finds a user with all their cards (cached in Redis)
     *
     * @param id the user ID to search for
     * @return the found user with cards as DTO
     * @throws com.innowise.userservice.exception.NotFoundException if user with given ID is not found
     */
    UserWithCardsDto findUserWithCardsById(Long id);
}