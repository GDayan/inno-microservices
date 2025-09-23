package com.innowise.controller;

import com.innowise.model.dto.CardDto;
import com.innowise.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/card")
public class CardController {
    private final CardService cardService;
    @PostMapping
    public ResponseEntity<CardDto> create(@Valid @RequestBody CardDto cardDto) {
        CardDto created = cardService.createCard(cardDto);
        return ResponseEntity.created(URI.create("/cards/" + created.getId())).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.getCardById(id));
    }

    @GetMapping("/batch")
    public ResponseEntity<List<CardDto>> getByIds(@RequestParam List<Long> ids) {
        return ResponseEntity.ok(cardService.getCardByIds(ids));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CardDto> update(@PathVariable Long id, @Valid @RequestBody CardDto cardDto) {
        return ResponseEntity.ok(cardService.updateCard(id, cardDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        cardService.deleteCard(id);
        return ResponseEntity.noContent().build();
    }
}
