package com.innowise.model.dto.request;

import com.innowise.model.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CardRequest {
    @NotNull(message = "User id cannot be null")
    private Long userId;

    @NotBlank(message = "Card number cannot be blank")
    @Size(max = 50, message = "Card number must not exceed 50 characters")
    private String number;

    @NotBlank(message = "Holder name cannot be blank")
    @Size(max = 50, message = "Holder name must not exceed 50 characters")
    private String holder;

    @NotNull(message = "Expiration date cannot be null")
    @FutureOrPresent(message = "Expiration date must be in the present or future")
    private LocalDate expirationDate;
}
