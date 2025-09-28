package com.example.interview.week2.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Slf4j
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

    private ResponseEntity<Object> buildResponse(int status, String code, String message, Map<String, Object> extra) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status);
        body.put("code", code);
        body.put("message", message);
        if (extra != null) {
            body.putAll(extra);
        }
        return new ResponseEntity<>(body, HttpStatus.valueOf(status));
    }

    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex,
            HttpServletRequest request) {
        log.error("Missing path variable", ex);
        String uri = request != null ? request.getRequestURI() : null;
        if (uri != null && uri.endsWith("/")) {
            return buildResponse(404, "404", "Resource not found", null);
        }
        return buildResponse(400, "400", "Missing path variable: " + ex.getVariableName(), null);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Object> handleMissingRequestParam(MissingServletRequestParameterException ex) {
        log.error("Missing request parameter", ex);
        return buildResponse(400, "400", "Missing request parameter: " + ex.getParameterName(), null);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.error("Method argument type mismatch", ex);
        return buildResponse(400, "400", "Invalid parameter: " + ex.getName(), null);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleMalformedJson(HttpMessageNotReadableException ex) {
        log.error("Malformed JSON request", ex);
        return buildResponse(400, "400", "Malformed JSON request", null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .collect(Collectors.toMap(f -> f.getField(), f -> f.getDefaultMessage(), (a, b) -> a));
        Map<String, Object> extra = new HashMap<>();
        extra.put("errors", errors);
        return buildResponse(400, "400", "Validation failed", extra);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
        String details = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining("; "));
        return buildResponse(400, "400", "Validation error: " + details, null);
    }

    @ExceptionHandler({ ResourceNotFoundException.class, NoResourceFoundException.class })
    public ResponseEntity<Object> handleNotFound(ResourceNotFoundException ex) {
        log.error("Resource not found", ex);
        return buildResponse(404, "404", "Resource not found", null);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Object> handleNoHandlerFound(NoHandlerFoundException ex) {
        log.error("No handler found. Cause: {}", ex.getMessage());
        return buildResponse(404, "404", "No handler found", null);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Object> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        log.error("Method not allowed", ex);
        return buildResponse(405, "405", "Method not allowed: " + ex.getMethod(), null);
    }

    @ExceptionHandler({ BadRequestException.class, DataIntegrityViolationException.class })
    public ResponseEntity<Object> handleBadRequest(Exception ex) {
        log.error("Bad request. Cause: {}", ex.getMessage());
        String msg = ex instanceof BadRequestException ? ex.getMessage() : "Request would violate data integrity";
        return buildResponse(400, "400", msg, null);
    }

    // @ExceptionHandler(AccessDeniedException.class)
    // public ResponseEntity<Object> handleAccessDenied(AccessDeniedException ex) {
    //     logger.debug("Access denied", ex);
    //     return buildResponse(403, "403", "Access denied", null);
    // }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralException(Exception ex) {
        log.error("Unhandled exception", ex);
        return buildResponse(500, "500", "Internal server error", null);
    }
}
