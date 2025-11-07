package com.innowise.userservice.controller;

import com.innowise.userservice.exception.NotFoundException;
import com.innowise.userservice.exception.UserAlreadyExistException;
import com.innowise.userservice.model.dto.UserDto;
import com.innowise.userservice.model.dto.UserWithCardsDto;
import com.innowise.userservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void testSaveUser_Success() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("test@example.com");

        Mockito.when(userService.save(any(UserDto.class))).thenReturn(userDto);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"test@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void testSaveUser_UserAlreadyExist() throws Exception {
        Mockito.when(userService.save(any(UserDto.class)))
                .thenThrow(new UserAlreadyExistException("User already exists", "test@example.com"));

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"test@example.com\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("User already exists: test@example.com"));
    }

    @Test
    void testFindUserById_Success() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("user@example.com");

        Mockito.when(userService.findById(1L)).thenReturn(userDto);

        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("user@example.com"));
    }

    @Test
    void testFindUserById_NotFound() throws Exception {
        Mockito.when(userService.findById(anyLong())).thenThrow(new NotFoundException("User", 1L));

        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found with id 1"));
    }

    @Test
    void testDeleteUser_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testFindUserWithCards_Success() throws Exception {
        UserWithCardsDto userWithCards = new UserWithCardsDto();
        userWithCards.setId(1L);
        userWithCards.setEmail("cards@example.com");
        userWithCards.setCards(Collections.emptyList());

        Mockito.when(userService.findUserWithCardsById(1L)).thenReturn(userWithCards);

        mockMvc.perform(get("/api/v1/users/1?include=cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("cards@example.com"))
                .andExpect(jsonPath("$.cards").isArray());
    }
}
