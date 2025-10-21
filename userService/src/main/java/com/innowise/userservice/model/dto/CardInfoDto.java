package com.innowise.userservice.model.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CardInfoDto {
    private Long id;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Card number is required")
    @Size(min = 16, max = 20, message = "Card number must be between 16 and 20 characters")
    private String number;

    @NotBlank(message = "Card holder is required")
    @Size(max = 200, message = "Card holder must not exceed 200 characters")
    private String holder;

    @Future(message = "Expiration date must be in the future")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate expirationDate;
}
