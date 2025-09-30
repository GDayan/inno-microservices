package com.innowise.model.dto.response;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CardResponse implements Serializable {
    private Long id;
    private String number;
    private String holder;
    private LocalDate expirationDate;
}
