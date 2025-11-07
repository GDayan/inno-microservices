package com.innowise.userservice.exception;

import com.innowise.userservice.controller.CardInfoController;
import com.innowise.userservice.service.CardInfoService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CardInfoController.class)
class GlobalExceptionHandlerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardInfoService cardInfoService;

    @Test
    void testHandleNotFoundException() throws Exception {
        Mockito.when(cardInfoService.findCardById(anyLong()))
                .thenThrow(new NotFoundException("Card not found"));

        mockMvc.perform(get("/api/v1/users/1/cards/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Resource Not Found"))
                .andExpect(jsonPath("$.message").value("Card not found"))
                .andExpect(jsonPath("$.documentationUrl").value("/api/docs/errors/404"));
    }

    @Test
    void testHandleCardValidationException() throws Exception {
        Mockito.when(cardInfoService.findCardById(anyLong()))
                .thenThrow(new CardValidationException("Invalid card number"));

        mockMvc.perform(get("/api/v1/users/1/cards/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Card Validation Failed"))
                .andExpect(jsonPath("$.message").value("Invalid card number"))
                .andExpect(jsonPath("$.documentationUrl").value("/api/docs/errors/400"));
    }

    @Test
    void testHandleUserAlreadyExistException() throws Exception {
        Mockito.when(cardInfoService.findCardById(anyLong()))
                .thenThrow(new UserAlreadyExistException("User already exists"));

        mockMvc.perform(get("/api/v1/users/1/cards/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("User Already Exists"))
                .andExpect(jsonPath("$.message").value("User already exists"))
                .andExpect(jsonPath("$.documentationUrl").value("/api/docs/errors/409"));
    }

    @Test
    void testHandleIllegalArgumentException() throws Exception {
        Mockito.when(cardInfoService.findCardById(anyLong()))
                .thenThrow(new IllegalArgumentException("Illegal argument"));

        mockMvc.perform(get("/api/v1/users/1/cards/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Invalid Argument"))
                .andExpect(jsonPath("$.message").value("Illegal argument"))
                .andExpect(jsonPath("$.documentationUrl").value("/api/docs/errors/400"));
    }

    @Test
    void testHandleGeneralException() throws Exception {
        Mockito.when(cardInfoService.findCardById(anyLong()))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(get("/api/v1/users/1/cards/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred"))
                .andExpect(jsonPath("$.documentationUrl").value("/api/docs/errors/500"));
    }
}
