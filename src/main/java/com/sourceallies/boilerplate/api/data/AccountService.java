package com.sourceallies.boilerplate.api.data;

import com.sourceallies.boilerplate.api.data.entities.Account;
import com.sourceallies.boilerplate.api.data.entities.AccountHierarchy;
import com.sourceallies.boilerplate.api.data.entities.CreateAccountRequest;
import com.sourceallies.boilerplate.api.data.entities.UpdateAccountRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AccountService {
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Iterable<Account> getAll() {
        return accountRepository.findAll();
    }

    public Account getById(Integer id) {
        return accountRepository.findById(id).orElseThrow(() -> new AccountNotFoundException(id));
    }
    public Account create(CreateAccountRequest request) {
        var account = Account.builder()
            .name(request.getName())
            .build();
        return accountRepository.save(account);
    }

    public Account update(Integer id, UpdateAccountRequest request) {
        Account account = getById(id);
        account.setName(request.getName());
        return accountRepository.save(account);
    }

    @Transactional
    public void delete(Integer id) {
        accountRepository.removeChildrenFromAccount(id);
        accountRepository.deleteById(id);
    }

    private AccountHierarchy getHierarchy(Account account) {
        return AccountHierarchy
            .builder()
            .id(account.getId())
            .name(account.getName())
            .children(IterableUtils
                .toList(accountRepository.getChildren(account.getId()))
                .stream()
                .map(this::getHierarchy)
                .toList()
            )
            .build()
            ;
    }

    public AccountHierarchy getHierarchy(Integer accountId) {
        return getHierarchy(getById(accountId));
    }

    @Transactional
    public void addChildAccount(Integer parentId, Integer childId) {
        accountRepository.addChildAccount(parentId, childId);
    }
}
