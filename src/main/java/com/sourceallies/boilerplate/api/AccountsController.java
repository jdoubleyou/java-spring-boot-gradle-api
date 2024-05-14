package com.sourceallies.boilerplate.api;

import com.sourceallies.boilerplate.api.data.AccountService;
import com.sourceallies.boilerplate.api.data.entities.Account;
import com.sourceallies.boilerplate.api.data.entities.AccountHierarchy;
import com.sourceallies.boilerplate.api.data.entities.CreateAccountRequest;
import com.sourceallies.boilerplate.api.data.entities.UpdateAccountRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
public class AccountsController {
    private final AccountService accountService;

    public AccountsController(AccountService accountService) {
        this.accountService = accountService;
    }
    @GetMapping("/accounts")
    public Flux<Account> getAllAccounts() {
        return accountService.getAll();
    }

    @GetMapping("/accounts/{accountId}")
    public Mono<Account> getAccountById(@PathVariable Integer accountId) {
        return accountService.getById(accountId);
    }

    @PostMapping("/accounts")
    public Mono<ResponseEntity<Void>> createAccount(@RequestBody CreateAccountRequest body) {
        return accountService.create(body)
            .map(account -> ResponseEntity
                .created(URI.create("/accounts/%s".formatted(account.getId()))).build()
            );
    }

    @PutMapping("/accounts/{accountId}")
    public Mono<ResponseEntity<Void>> updateAccount(@PathVariable Integer accountId, @RequestBody UpdateAccountRequest request) {
        return accountService.update(accountId, request)
            .map(unused -> ResponseEntity.noContent().build());
    }

    @DeleteMapping("/accounts/{accountId}")
    public Mono<ResponseEntity<Void>> deleteAccount(@PathVariable Integer accountId) {
        return accountService.delete(accountId)
            .thenReturn(ResponseEntity.noContent().build());
    }

    @GetMapping("/accounts/{accountId}/children")
    public Mono<AccountHierarchy> getHierarchy(@PathVariable Integer accountId) {
        return accountService.getHierarchy(accountId);
    }

    @PutMapping("/accounts/{accountId}/children/{childAccount}")
    public Mono<ResponseEntity<Void>> addChild(@PathVariable Integer accountId, @PathVariable Integer childAccount) {
        return accountService.addChildAccount(accountId, childAccount)
            .thenReturn(ResponseEntity.noContent().build());
    }
}
