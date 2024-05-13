package com.sourceallies.boilerplate.api.errors;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;

@RestControllerAdvice
@Slf4j
public class BoilerplateErrorHandler /*extends ResponseEntityExceptionHandler*/ {
    @Builder
    @Jacksonized
    @Data
    static class Error {
        String field;
        String reason;
        Object invalidValue;
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseStatusException methodArgumentNotValid(MethodArgumentNotValidException exception) {
        var statusException = new ResponseStatusException(HttpStatus.BAD_REQUEST, "The given request is not valid for the endpoint.");

        var errors = new ArrayList<Error>();

        exception.getFieldErrors().forEach(err -> {
            errors.add(Error.builder()
                .field(err.getField())
                .reason(err.getDefaultMessage())
                .invalidValue(err.getRejectedValue())
                .build());
        });
        statusException.getBody().setProperty("errors", errors);
        return statusException;
    }


    @ExceptionHandler({BoilerplateNotFoundException.class})
    public ResponseStatusException resourceNotFound(BoilerplateNotFoundException exception) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler({DataIntegrityViolationException.class})
    public ResponseStatusException dataIntegrityViolation(DataIntegrityViolationException exception) {
        return new ResponseStatusException(HttpStatus.CONFLICT, "A conflict occurred with the request.");
    }

    @ExceptionHandler({BoilerplateConflictingResourceException.class})
    public ResponseStatusException conflictingResource(BoilerplateConflictingResourceException exception) {
        return new ResponseStatusException(HttpStatus.CONFLICT, exception.getMessage());
    }
}
