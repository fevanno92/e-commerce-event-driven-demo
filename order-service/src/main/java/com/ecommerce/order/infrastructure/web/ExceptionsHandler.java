package com.ecommerce.order.infrastructure.web;

import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.ecommerce.common.application.dto.ErrorDTO;
import com.ecommerce.order.application.exception.InvalidProductException;
import com.ecommerce.order.domain.exception.InvalidOrderException;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class ExceptionsHandler extends ResponseEntityExceptionHandler {

        @ExceptionHandler({ InvalidProductException.class, InvalidOrderException.class })
        public ResponseEntity<ErrorDTO> handleDomainException(RuntimeException exception) {
                log.error("Domain validation error: {}", exception.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorDTO.builder()
                                .code(HttpStatus.BAD_REQUEST.name())
                                .message(exception.getMessage())
                                .timestamp(LocalDateTime.now())
                                .build());
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorDTO> handleGenericException(Exception exception) {
                log.error("Unexpected error", exception);

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorDTO.builder()
                                .code(HttpStatus.INTERNAL_SERVER_ERROR.name())
                                .message("An unexpected error occurred. Please contact support.")
                                .timestamp(LocalDateTime.now())
                                .build());
        }

        @Override
        protected @Nullable ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                        HttpHeaders headers, HttpStatusCode status, WebRequest request) {
                String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                                .collect(Collectors.joining(", "));

                log.error("Validation error: {}", errorMessage);

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorDTO.builder()
                                .code(HttpStatus.BAD_REQUEST.name())
                                .message(errorMessage)
                                .timestamp(LocalDateTime.now())
                                .build());
        }

}
