package com.sourceallies.boilerplate.api.coffee;

import com.sourceallies.boilerplate.api.coffee.entities.*;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Flux<Customer> getAll() {
        return customerRepository.findAll();
    }

    public Mono<Customer> getById(Integer id) {
        return customerRepository
            .findById(id)
            .switchIfEmpty(Mono.error(new MenuNotFoundException(id)));
    }

    public Mono<Customer> create(CreateCustomerRequest request) {
        var customer = Customer.builder()
            .name(request.getName())
            .createdDate(ZonedDateTime.now(ZoneOffset.UTC))
            .build();
        return customerRepository.create(customer);
    }

    public Mono<Customer> update(Integer id, UpdateCustomerRequest request) {
        return customerRepository
            .findById(id)
            .switchIfEmpty(Mono.error(new CustomerNotFoundException(id)))
            .flatMap(customer -> {
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
                return customerRepository.update(customer);
            });
    }

    public Mono<Void> delete(Integer id) {
        return customerRepository.deleteById(id);
    }
}
