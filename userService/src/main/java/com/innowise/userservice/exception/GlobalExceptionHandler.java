package com.innowise.userservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CardValidationException.class)
    public ResponseEntity<ErrorApiDto> handleCardValidationException(
            CardValidationException ex, HttpServletRequest request) {
        log.warn("Card validation failed: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Card Validation Failed",
                ex.getMessage(),
                request
        ));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorApiDto> handleNotFoundException(
            NotFoundException ex, HttpServletRequest request) {
        log.info("Resource not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildErrorResponse(
                HttpStatus.NOT_FOUND,
                "Resource Not Found",
                ex.getMessage(),
                request
        ));
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<ErrorApiDto> handleUserAlreadyExistException(
            RuntimeException ex, HttpServletRequest request) {
        log.warn("User creation conflict: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(buildErrorResponse(
                HttpStatus.CONFLICT,
                "User Already Exists",
                ex.getMessage(),
                request
        ));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorApiDto> handleBadRequestException(
            BadRequestException ex, HttpServletRequest request) {
        log.warn("Bad request: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                ex.getMessage(),
                request
        ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorApiDto> handleIllegalArgumentException(
            IllegalArgumentException ex, HttpServletRequest request) {
        log.warn("Illegal argument: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid Argument",
                ex.getMessage(),
                request
        ));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            @Nullable MethodArgumentNotValidException ex,
            @Nullable HttpHeaders headers,
            @Nullable HttpStatusCode status,
            @Nullable WebRequest request) {

        String requestDescription = request != null ? request.getDescription(false) : "unknown";
        log.warn("Validation failed for request: {}", requestDescription);

        if (ex == null || ex.getBindingResult() == null) {
            String path = extractPath(request);
            ErrorApiDto error = ErrorApiDto.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.BAD_REQUEST.value())
                    .error("Validation Failed")
                    .message("Validation error details are unavailable")
                    .path(path)
                    .documentationUrl("/api/docs/validation-errors")
                    .build();
            return ResponseEntity.badRequest().body(error);
        }

        Map<String, String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> fieldError.getDefaultMessage() != null
                                ? fieldError.getDefaultMessage()
                                : "Invalid value",
                        (existing, replacement) -> existing
                ));

        String path = extractPath(request);
        ErrorApiDto error = ErrorApiDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Failed")
                .message("One or more fields failed validation")
                .path(path)
                .errors(fieldErrors)
                .documentationUrl("/api/docs/validation-errors")
                .build();

        return ResponseEntity.badRequest().body(error);
    }

    private String extractPath(@Nullable WebRequest request) {
        if (request instanceof ServletWebRequest servletRequest) {
            return servletRequest.getRequest().getRequestURI();
        }
        return "/unknown";
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorApiDto> handleGeneralException(
            Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception occurred: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "An unexpected error occurred",
                request
        ));
    }

    private ErrorApiDto buildErrorResponse(HttpStatus status, String error,
                                           String message, HttpServletRequest request) {
        return ErrorApiDto.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(error)
                .message(message)
                .path(request.getRequestURI())
                .documentationUrl("/api/docs/errors/" + status.value())
                .build();
    }
}
