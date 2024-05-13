package com.sourceallies.boilerplate.api.errors;

public class BoilerplateNotFoundException extends RuntimeException {
    public BoilerplateNotFoundException(String message) {
        super(message);
    }
}
