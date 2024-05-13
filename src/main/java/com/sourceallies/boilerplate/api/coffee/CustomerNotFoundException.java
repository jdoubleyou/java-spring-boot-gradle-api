package com.sourceallies.boilerplate.api.coffee;

import com.sourceallies.boilerplate.api.errors.BoilerplateNotFoundException;

public class CustomerNotFoundException extends BoilerplateNotFoundException {
    public CustomerNotFoundException(Integer authorId) {
        super("No customer found for id %s".formatted(authorId));
    }
}
