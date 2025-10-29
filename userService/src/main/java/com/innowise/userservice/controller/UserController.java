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
     * Retrieves users based on optional filters:
     *  - GET /api/v1/users?email={email} → returns one user by email
     *  - GET /api/v1/users?ids=1,2,3    → returns list of users by ids
     *
     * If neither parameter is provided → returns 400 Bad Request
     * If both are provided simultaneously → returns 400 Bad Request
     *
     * @param ids   optional list of ids
     * @param email optional email filter
     * @return ResponseEntity containing user data or list of users
     */
    @GetMapping
    public ResponseEntity<?> find(
            @RequestParam(required = false) List<Long> ids,
            @RequestParam(required = false) String email
    ) {
        if (email != null && ids != null) {
            return ResponseEntity.badRequest().body("Please provide either 'email' OR 'ids', not both.");
        }

        if (email != null) {
            UserDto user = userService.findUserByEmail(email);
            return ResponseEntity.ok(user);
        }

        if (ids != null && !ids.isEmpty()) {
            List<UserDto> users = userService.findByIds(ids);
            return ResponseEntity.ok(users);
        }

        return ResponseEntity.badRequest().body("Please specify either 'email' or 'ids' query parameter");
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
     * Retrieves a user along with all their cards.
     * Data may be cached in Redis.
     *
     * @param id the ID of the user to retrieve with their associated cards
     * @return ResponseEntity containing the user with cards and HTTP status 200 (OK)
     */
    @GetMapping("/{id}/with-cards")
    public ResponseEntity<UserWithCardsDto> findUserWithCardsById(@PathVariable("id") Long id) {
        UserWithCardsDto userWithCards = userService.findUserWithCardsById(id);
        return ResponseEntity.ok(userWithCards);
    }
}
