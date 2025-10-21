package com.innowise.userservice.model.entity;

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
public class CardInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "number", length = 20, nullable = false)
    private String number;

    @Column(name = "holder", length = 200, nullable = false)
    private String holder;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;
}

