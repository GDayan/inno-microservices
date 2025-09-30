package com.innowise.controller;

import com.innowise.model.dto.request.*;
import com.innowise.model.dto.response.*;
import com.innowise.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> save(@RequestBody @Valid UserRequest userRequest){
        UserResponse userResponse = userService.save(userRequest);
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable("id") Long id) {
        UserResponse user = userService.findById(id);

        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> findByFilter(
            @RequestParam(required = false, defaultValue = "pageable") String filter,
            @RequestParam(required = false) List<Long> ids,
            @RequestParam(required = false) String email,
            Pageable pageable) {

        return switch (filter) {
            case "ids" -> ResponseEntity.ok(userService.findByIds(ids));
            case "email" -> ResponseEntity.ok(List.of(userService.findUserByEmail(email)));
            default -> ResponseEntity.ok(userService.findAll(pageable));
        };
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable("id") Long id, @RequestBody @Valid UserRequest userRequest){
        UserResponse user = userService.updateById(id, userRequest);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id){
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
