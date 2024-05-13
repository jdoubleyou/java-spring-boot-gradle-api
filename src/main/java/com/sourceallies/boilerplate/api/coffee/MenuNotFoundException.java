package com.sourceallies.boilerplate.api.coffee;

import com.sourceallies.boilerplate.api.errors.BoilerplateNotFoundException;

public class MenuNotFoundException extends BoilerplateNotFoundException {
    public MenuNotFoundException(Integer authorId) {
        super("No menu found for id %s".formatted(authorId));
    }
}
