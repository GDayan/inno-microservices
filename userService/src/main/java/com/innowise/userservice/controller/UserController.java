package com.innowise.userservice.controller;

import com.innowise.userservice.model.dto.UserDto;
import com.innowise.userservice.model.dto.UserWithCardsDto;
import com.innowise.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing users.
 * Provides endpoints for creating, retrieving, updating and deleting users.
 * All endpoints are prefixed with '/api/v1/users'.
 * Works with {@link UserDto} as request/response payloads.
 *
 * @author Dayan Gutlyyev
 * @since 2025
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    /**
     * Creates a new user.
     *
     * @param userDto the user data to create, validated according to bean validation constraints
     * @return ResponseEntity containing the created user and HTTP status 200 (OK)
     * @throws com.innowise.userservice.exception.UserAlreadyExistException if email already exists
     */
    @PostMapping
    public ResponseEntity<UserDto> save(@RequestBody @Valid UserDto userDto) {
        UserDto user = userService.save(userDto);
        return ResponseEntity.ok(user);
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user to retrieve
     * @return ResponseEntity containing the user and HTTP status 200 (OK)
     * @throws com.innowise.userservice.exception.NotFoundException if user with given ID is not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> findById(@PathVariable("id") Long id) {
        UserDto user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Retrieves multiple users by their IDs.
     *
     * @param ids list of user IDs to retrieve
     * @return ResponseEntity containing list of users and HTTP status 200 (OK)
     */
//    @GetMapping
//    public ResponseEntity<List<UserDto>> findByIds(@RequestParam("ids") List<Long> ids) {
//        List<UserDto> users = userService.findByIds(ids);
//        return ResponseEntity.ok(users);
//    }

    /**
     * Retrieves a user by their email address.
     *
     * @param email the email address to search for
     * @return ResponseEntity containing the user and HTTP status 200 (OK)
     * @throws com.innowise.userservice.exception.NotFoundException if user with given email is not found
     */
    @GetMapping("/by-email")
    public ResponseEntity<UserDto> findUserByEmail(@RequestParam("email") String email) {
        UserDto user = userService.findUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    /**
     * Updates an existing user.
     *
     * @param id the ID of the user to update
     * @param userDto the updated user data
     * @return ResponseEntity containing the updated user and HTTP status 200 (OK)
     * @throws com.innowise.userservice.exception.NotFoundException if user with given ID is not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> update(@PathVariable("id") Long id, @RequestBody UserDto userDto) {
        UserDto updatedUser = userService.update(id, userDto);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Deletes a user by their ID, including all associated cards.
     *
     * @param id the ID of the user to delete
     * @return ResponseEntity with no content and HTTP status 204 (NO_CONTENT)
     * @throws com.innowise.userservice.exception.NotFoundException if user with given ID is not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        userService.deleteByIdNative(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves a user with all their cards (cached in Redis)
     *
     * @param id the ID of the user to retrieve with cards
     * @return ResponseEntity containing the user with cards and HTTP status 200 (OK)
     * @throws com.innowise.userservice.exception.NotFoundException if user with given ID is not found
     */
    @GetMapping("/{id}/with-cards")
    public ResponseEntity<UserWithCardsDto> findUserWithCardsById(@PathVariable("id") Long id) {
        UserWithCardsDto userWithCards = userService.findUserWithCardsById(id);
        return ResponseEntity.ok(userWithCards);
    }
}