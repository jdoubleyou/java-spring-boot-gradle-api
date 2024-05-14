package com.sourceallies.boilerplate.api.data;

import com.sourceallies.boilerplate.api.errors.BoilerplateNotFoundException;

public class AccountNotFoundException extends BoilerplateNotFoundException {
    public AccountNotFoundException(Integer id) {
        super("No account found for id %s".formatted(id));
    }
}
