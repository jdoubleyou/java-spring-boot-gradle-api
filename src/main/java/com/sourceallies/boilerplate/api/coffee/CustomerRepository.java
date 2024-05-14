package com.sourceallies.boilerplate.api.coffee;

import com.sourceallies.boilerplate.api.coffee.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    @Override
    Customer save(Customer customer);

    @Override
    Optional<Customer> findById(Integer id);
}
