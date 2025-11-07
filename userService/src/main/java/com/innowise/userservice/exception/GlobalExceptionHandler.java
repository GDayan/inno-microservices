package com.innowise.userservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.validation.FieldError;

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

        ErrorApiDto error = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Card Validation Failed",
                ex.getMessage(),
                request
        );

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorApiDto> handleNotFoundException(
            NotFoundException ex, HttpServletRequest request) {

        log.info("Resource not found: {}", ex.getMessage());

        ErrorApiDto error = buildErrorResponse(
                HttpStatus.NOT_FOUND,
                "Resource Not Found",
                ex.getMessage(),
                request
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler({UserAlreadyExistException.class})
    public ResponseEntity<ErrorApiDto> handleUserAlreadyExistException(
            RuntimeException ex, HttpServletRequest request) {

        log.warn("User creation conflict: {}", ex.getMessage());

        ErrorApiDto error = buildErrorResponse(
                HttpStatus.CONFLICT,
                "User Already Exists",
                ex.getMessage(),
                request
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorApiDto> handleBadRequestException(
            BadRequestException ex, HttpServletRequest request) {

        log.warn("Bad request: {}", ex.getMessage());

        ErrorApiDto error = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                ex.getMessage(),
                request
        );

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorApiDto> handleIllegalArgumentException(
            IllegalArgumentException ex, HttpServletRequest request) {

        log.warn("Illegal argument: {}", ex.getMessage());

        ErrorApiDto error = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid Argument",
                ex.getMessage(),
                request
        );

        return ResponseEntity.badRequest().body(error);
    }

    @Override
    @NonNull
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            @NonNull MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {
        log.warn("Validation failed for request: {}", request.getDescription(false));
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
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
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




    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorApiDto> handleGeneralException(
            Exception ex, HttpServletRequest request) {

        log.error("Unhandled exception occurred: ", ex);

        ErrorApiDto error = buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "An unexpected error occurred",
                request
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
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