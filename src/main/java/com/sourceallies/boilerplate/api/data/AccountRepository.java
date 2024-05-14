package com.sourceallies.boilerplate.api.data;

import com.sourceallies.boilerplate.api.data.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    @Override
    Account save(Account author);

    @Override
    Optional<Account> findById(Integer integer);

    @Query(value = "SELECT acc.id, acc.name FROM accounts as acc JOIN account_hierarchy ach ON acc.id = ach.child WHERE ach.parent = :accountId", nativeQuery = true)
    Iterable<Account> getChildren(Integer accountId);

    @Query(value = "INSERT INTO account_hierarchy (parent, child) VALUES (?1, ?2)", nativeQuery = true)
    @Modifying
    void addChildAccount(Integer accountId, Integer childAccount);

    @Query(value = "DELETE FROM account_hierarchy WHERE parent = ?1", nativeQuery = true)
    @Modifying
    void removeChildrenFromAccount(Integer accountId);
}
