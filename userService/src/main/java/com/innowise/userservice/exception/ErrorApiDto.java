package com.innowise.userservice.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorApiDto {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime timestamp;

    private Integer status;
    private String error;
    private String message;
    private String path;
    private String traceId; // For distributed tracing

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, String> errors = new java.util.HashMap<>();

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String documentationUrl = "/api/docs/errors";
}