package com.innowise.model.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CardDto {
    private Long id;
    @NotBlank(message = "Number is required")
    private String number;
    @NotBlank(message = "Holder is required")
    private String holder;
    @Future(message = "Expiration date must be in the future")
    private LocalDate expirationDate;

    private Long userId;
}
