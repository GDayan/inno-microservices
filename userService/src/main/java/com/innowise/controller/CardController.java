package com.innowise.controller;

import com.innowise.model.dto.CardDto;
import com.innowise.model.dto.UserDto;
import com.innowise.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/card")
public class CardController {
    private final CardService cardService;

    @PostMapping
    public CardDto create(@Valid @RequestBody CardDto cardDto){
        return cardService.createCard(cardDto);
    }

    @GetMapping("/{id")
    public ResponseEntity<CardDto> findById(@PathVariable Long id){
        return ResponseEntity.ok(cardService.getCardById(id));
    }

    @GetMapping("/batch")
    public List<CardDto> findByIds(@PathVariable List<Long> ids){
        return cardService.getCardByIds(ids);
    }

    @PutMapping("/{id}")
    public CardDto update(@PathVariable Long id, @Valid @RequestBody CardDto cardDto){
        return cardService.updateCard(id, cardDto);
    }

    @DeleteMapping
    public void delete(@PathVariable Long id){
        cardService.deleteCard(id);
    }
}
