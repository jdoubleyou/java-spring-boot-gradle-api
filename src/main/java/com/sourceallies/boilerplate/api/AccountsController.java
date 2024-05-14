package com.sourceallies.boilerplate.api;

import com.sourceallies.boilerplate.api.data.AccountService;
import com.sourceallies.boilerplate.api.data.entities.Account;
import com.sourceallies.boilerplate.api.data.entities.AccountHierarchy;
import com.sourceallies.boilerplate.api.data.entities.CreateAccountRequest;
import com.sourceallies.boilerplate.api.data.entities.UpdateAccountRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class AccountsController {
    private final AccountService accountService;

    public AccountsController(AccountService accountService) {
        this.accountService = accountService;
    }
    @GetMapping("/accounts")
    public Iterable<Account> getAllAccounts() {
        return accountService.getAll();
    }

    @GetMapping("/accounts/{accountId}")
    public Account getAccountById(@PathVariable Integer accountId) {
        return accountService.getById(accountId);
    }

    @PostMapping("/accounts")
    public ResponseEntity<Void> createAccount(@RequestBody CreateAccountRequest body) {
        var account = accountService.create(body);
        return ResponseEntity.created(URI.create("/accounts/%s".formatted(account.getId()))).build();
    }

    @PutMapping("/accounts/{accountId}")
    public ResponseEntity<Void> updateAccount(@PathVariable Integer accountId, @RequestBody UpdateAccountRequest request) {
        accountService.update(accountId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/accounts/{accountId}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Integer accountId) {
        accountService.delete(accountId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/accounts/{accountId}/children")
    public AccountHierarchy getHierarchy(@PathVariable Integer accountId) {
        return accountService.getHierarchy(accountId);
    }

    @PutMapping("/accounts/{accountId}/children/{childAccount}")
    public ResponseEntity<Void> addChild(@PathVariable Integer accountId, @PathVariable Integer childAccount) {
        accountService.addChildAccount(accountId, childAccount);
        return ResponseEntity.noContent().build();
    }
}
