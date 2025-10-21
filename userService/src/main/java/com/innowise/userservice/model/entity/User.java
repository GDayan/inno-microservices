package com.innowise.userservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "surname", length = 100, nullable = false)
    private String surname;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "email", length = 255, nullable = false, unique = true)
    private String email;

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CardInfo> cardInfos = new ArrayList<>();

    public void addCard(CardInfo card) {
        cardInfos.add(card);
        card.setUser(this);
    }

    public void removeCard(CardInfo card) {
        cardInfos.remove(card);
        card.setUser(null);
    }

    public void removeAllCards() {
        cardInfos.forEach(card -> card.setUser(null));
        cardInfos.clear();
    }
}
