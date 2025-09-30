package com.innowise.exception;

import lombok.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ErrorApiDto {
    private LocalDateTime timestamp;
    private Integer status;
    private String error;
    private String message;
    private String path;
    @Builder.Default
    private Map<String, String> errors = new HashMap<>();
}
