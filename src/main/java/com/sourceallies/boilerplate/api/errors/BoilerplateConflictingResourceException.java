package com.sourceallies.boilerplate.api.errors;

public class BoilerplateConflictingResourceException extends RuntimeException {
    public BoilerplateConflictingResourceException(String message) {
        super(message);
    }
}
