package com.sourceallies.boilerplate.api.coffee;

import com.sourceallies.boilerplate.api.coffee.entities.*;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Iterable<Customer> getAll() {
        return customerRepository.findAll();
    }

    public Customer getByIdOrThrow(Integer id) {
        return customerRepository
            .findById(id)
            .orElseThrow(() -> new MenuNotFoundException(id));
    }

    public Customer create(CreateCustomerRequest request) {
        var author = Customer.builder()
            .name(request.getName())
            .createdDate(ZonedDateTime.now(ZoneOffset.UTC))
            .build();
        return customerRepository.save(author);
    }

    public Customer update(Integer id, UpdateCustomerRequest request) {
        Customer customer = customerRepository.findById(id).orElseThrow(() -> new CustomerNotFoundException(id));
        boolean hasChanges = false;
        if (
            !ObjectUtils.nullSafeEquals(
                customer.getName(),
                request.getName()
            )
        ) {
            customer.setName(request.getName());
            hasChanges = true;
        }

        if (hasChanges) {
            customer.setLastUpdatedDate(ZonedDateTime.now(ZoneOffset.UTC));
        }
        return customerRepository.save(customer);
    }

    public void delete(Integer id) {
        customerRepository.deleteById(id);
    }
}
