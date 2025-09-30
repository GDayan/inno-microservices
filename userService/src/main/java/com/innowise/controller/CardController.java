package com.innowise.controller;

import com.innowise.model.dto.request.CardRequest;
import com.innowise.model.dto.response.CardResponse;
import com.innowise.model.entity.Card;
import com.innowise.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cards")
public class CardController {
    private final CardService cardService;

    @PostMapping
    public ResponseEntity<CardResponse> save(@RequestBody @Valid CardRequest cardRequest){
        CardResponse card = cardService.save(cardRequest);
        return ResponseEntity.ok(card);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardResponse> findById(@PathVariable Long id) {
        CardResponse card = cardService.findById(id);

        return ResponseEntity.ok(card);
    }

    @GetMapping
    public ResponseEntity<List<CardResponse>> findByIds(@RequestParam List<Long> ids) {
        List<CardResponse> cardsInfo = cardService.findByIds(ids);
        return ResponseEntity.ok(cardsInfo);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CardResponse> updateById(@PathVariable("id") Long id, @RequestBody @Valid CardRequest cardRequest) {
        CardResponse cardInfo = cardService.updateById(id, cardRequest);

        return ResponseEntity.ok(cardInfo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") Long id){
        cardService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
