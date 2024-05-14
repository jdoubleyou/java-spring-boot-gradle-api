package com.sourceallies.boilerplate.api.data;

import com.sourceallies.boilerplate.api.data.entities.Account;
import io.r2dbc.spi.Readable;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class AccountRepository {
    DatabaseClient databaseClient;

    public AccountRepository(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    public Mono<Account> create(Account account) {
        return databaseClient
            .sql("INSERT INTO accounts (id, name) VALUES (nextval('accounts_id_seq'), :name) RETURNING id;")
            .bind("name", account.getName())
            .map(row -> account.toBuilder()
                .id(row.get("id", Integer.class))
                .build())
            .first()
            ;
    }

    public Mono<Account> update(Account account) {
        return databaseClient
            .sql("UPDATE accounts SET name = :name WHERE id = :id;")
            .bind("id", account.getId())
            .bind("name", account.getName())
            .then()
            .thenReturn(account)
            ;
    }

    public Flux<Account> findAll() {
        return databaseClient
            .sql("SELECT id, name FROM accounts")
            .map(this::mapAccount)
            .all()
            ;
    }

    public Mono<Account> findById(Integer id) {
        return databaseClient
            .sql("SELECT id, name FROM accounts WHERE id = :accountId")
            .bind("accountId", id)
            .map(this::mapAccount)
            .first()
            ;
    }

    public Flux<Account> getChildren(Integer accountId) {
        return databaseClient
            .sql("SELECT acc.id, acc.name FROM accounts as acc JOIN account_hierarchy ach ON acc.id = ach.child WHERE ach.parent = :accountId")
            .bind("accountId", accountId)
            .map(this::mapAccount)
            .all()
            ;
    }

    private Account mapAccount(Readable row) {
        return  Account.builder()
            .id(row.get("id", Integer.class))
            .name(row.get("name", String.class))
            .build()
            ;
    }

    public Mono<Void> addChildAccount(Integer accountId, Integer childId) {
        return databaseClient
            .sql("INSERT INTO account_hierarchy (parent, child) VALUES (:parentId, :childId)")
            .bind("parentId", accountId)
            .bind("childId", childId)
            .then();
    }

    public Mono<Void> removeChildrenFromAccount(Integer accountId) {
        return databaseClient
            .sql("DELETE FROM account_hierarchy WHERE parent = :accountId")
            .bind("accountId", accountId)
            .then();
    }

    public Mono<Void> deleteById(Integer accountId) {
        return databaseClient
            .sql("DELETE FROM accounts WHERE id = :accountId;")
            .bind("accountId", accountId)
            .then();
    }
}
