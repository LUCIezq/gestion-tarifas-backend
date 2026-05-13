package com.sastreria.gestiondeprecios.config;

import com.sastreria.gestiondeprecios.util.AbstractException;
import com.sastreria.gestiondeprecios.util.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handle(MethodArgumentNotValidException exception,
                        HttpServletRequest request) {

                Map<String, String> errors = exception.getBindingResult().getFieldErrors().stream()
                                .collect(Collectors.toMap(
                                                FieldError::getField,
                                                FieldError::getDefaultMessage,
                                                (existing, newValue) -> existing + ", " + newValue));

                ErrorResponse response = ErrorResponse.builder()
                                .message("Error de validacion")
                                .status(HttpStatus.BAD_REQUEST.value())
                                .timestamp(LocalDateTime.now())
                                .method(request.getMethod())
                                .path(request.getRequestURI())
                                .errors(errors)
                                .build();

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException exception,
                        HttpServletRequest request) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ErrorResponse.builder()
                                                .message(exception.getMessage())
                                                .status(HttpStatus.BAD_REQUEST.value())
                                                .timestamp(LocalDateTime.now())
                                                .method(request.getMethod())
                                                .path(request.getRequestURI())
                                                .build());
        }

        @ExceptionHandler(ConstraintViolationException.class)
        public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex,
                        HttpServletRequest request) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(
                                                ErrorResponse.builder()
                                                                .message(ex.getMessage())
                                                                .status(HttpStatus.BAD_REQUEST.value())
                                                                .timestamp(LocalDateTime.now())
                                                                .method(request.getMethod())
                                                                .path(request.getRequestURI())
                                                                .build());
        }

        @ExceptionHandler(AbstractException.class)
        public ResponseEntity<ErrorResponse> handleAbstractException(AbstractException exception,
                        HttpServletRequest request) {
                return ResponseEntity.status(exception.getStatus()).body(
                                ErrorResponse.builder()
                                                .message(exception.getMessage())
                                                .status(exception.getStatus().value())
                                                .timestamp(LocalDateTime.now())
                                                .method(request.getMethod())
                                                .path(request.getRequestURI())
                                                .build());
        }

        @ExceptionHandler(DataIntegrityViolationException.class)
        public ResponseEntity<ErrorResponse> handleBaseDataConstraint(DataIntegrityViolationException ex,
                        HttpServletRequest request) {
                log.error("Error de integridad en BD", ex);
                return ResponseEntity.status(
                                HttpStatus.BAD_REQUEST)
                                .body(
                                                ErrorResponse.builder()
                                                                .message("Error de integridad de datos")
                                                                .status(HttpStatus.BAD_REQUEST.value())
                                                                .timestamp(LocalDateTime.now())
                                                                .path(request.getRequestURI())
                                                                .build());
        }
}
