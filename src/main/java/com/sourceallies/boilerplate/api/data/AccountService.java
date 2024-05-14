package com.sourceallies.boilerplate.api.data;

import com.sourceallies.boilerplate.api.data.entities.Account;
import com.sourceallies.boilerplate.api.data.entities.AccountHierarchy;
import com.sourceallies.boilerplate.api.data.entities.CreateAccountRequest;
import com.sourceallies.boilerplate.api.data.entities.UpdateAccountRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class AccountService {
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Flux<Account> getAll() {
        return accountRepository.findAll();
    }

    public Mono<Account> getById(Integer id) {
        return accountRepository.findById(id)
            .switchIfEmpty(Mono.error(new AccountNotFoundException(id)));
    }
    public Mono<Account> create(CreateAccountRequest request) {
        var account = Account.builder()
            .name(request.getName())
            .build();
        return accountRepository.create(account);
    }

    public Mono<Account> update(Integer id, UpdateAccountRequest request) {
        return getById(id)
            .map(account -> account.toBuilder().name(request.getName()).build())
            .flatMap(accountRepository::update)
            ;
    }

    @Transactional
    public Mono<Void> delete(Integer id) {
        return accountRepository
            .removeChildrenFromAccount(id)
            .then(Mono.defer(() -> accountRepository.deleteById(id)));
    }

    private Mono<AccountHierarchy> getHierarchy(Account account) {
        return accountRepository.getChildren(account.getId())
            .flatMap(this::getHierarchy)
            .collectList()
            .map(childrenAccounts -> AccountHierarchy.builder()
                .id(account.getId())
                .name(account.getName())
                .children(childrenAccounts)
                .build());
    }

    public Mono<AccountHierarchy> getHierarchy(Integer accountId) {
        return getById(accountId).flatMap(this::getHierarchy);
    }

    @Transactional
    public Mono<Void> addChildAccount(Integer parentId, Integer childId) {
        return accountRepository.addChildAccount(parentId, childId);
    }
}
