package com.innowise.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.userservice.model.dto.CardInfoDto;
import com.innowise.userservice.service.CardInfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CardInfoController.class)
class CardInfoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardInfoService cardInfoService;

    @Autowired
    private ObjectMapper objectMapper;

    private CardInfoDto cardDto;

    @BeforeEach
    void setUp() {
        cardDto = new CardInfoDto();
        cardDto.setId(1L);
        cardDto.setUserId(1L);
        cardDto.setNumber("1234567890123456");
        cardDto.setHolder("Test User");
    }

    @Test
    void testSaveCard() throws Exception {
        Mockito.when(cardInfoService.saveCard(any(CardInfoDto.class))).thenReturn(cardDto);

        mockMvc.perform(post("/api/v1/users/1/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(cardDto.getId()))
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    void testFindCardsByUserId() throws Exception {
        List<CardInfoDto> cards = Arrays.asList(cardDto);
        Mockito.when(cardInfoService.findCardsByUserId(1L)).thenReturn(cards);

        mockMvc.perform(get("/api/v1/users/1/cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(cardDto.getId()))
                .andExpect(jsonPath("$[0].userId").value(1));
    }

    @Test
    void testFindCardById() throws Exception {
        Mockito.when(cardInfoService.findCardById(1L)).thenReturn(cardDto);

        mockMvc.perform(get("/api/v1/users/1/cards/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    void testUpdateCard() throws Exception {
        CardInfoDto updatedCard = new CardInfoDto();
        updatedCard.setId(1L);
        updatedCard.setUserId(1L);
        updatedCard.setNumber("6543210987654321");
        updatedCard.setHolder("Updated User");

        Mockito.when(cardInfoService.updateCard(eq(1L), any(CardInfoDto.class))).thenReturn(updatedCard);

        mockMvc.perform(put("/api/v1/users/1/cards/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCard)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cardNumber").value("6543210987654321"))
                .andExpect(jsonPath("$.cardHolderName").value("Updated User"));
    }

    @Test
    void testDeleteCard() throws Exception {
        Mockito.doNothing().when(cardInfoService).deleteCard(1L);

        mockMvc.perform(delete("/api/v1/users/1/cards/1"))
                .andExpect(status().isNoContent());
    }
}
