package com.innowise.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "card_info")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 50, nullable = false)
    private String number;
    @Column(length = 50, nullable = false)
    private String holder;
    @Column(nullable = false)
    private LocalDate expirationDate;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}

