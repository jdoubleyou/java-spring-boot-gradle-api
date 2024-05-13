package com.sourceallies.boilerplate.api.coffee;

import com.sourceallies.boilerplate.api.coffee.entities.Customer;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Repository
public class CustomerRepository {

    DatabaseClient databaseClient;

    public CustomerRepository(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    public Mono<Customer> create(Customer customer) {
        return databaseClient
            .sql("INSERT INTO customers (id, name, created_date, last_updated_date) VALUES (nextval('customers_id_seq'), :name, :created, null) RETURNING id;")
            .bind("name", customer.getName())
            .bind("created", customer.getCreatedDate())
            .map(row -> customer.toBuilder()
                .id(row.get("id", Integer.class))
                .build())
            .first()
            ;
    }

    public Mono<Customer> update(Customer customer) {
        return databaseClient
            .sql("UPDATE customers SET name = :name, created_date = :created, last_updated_date = :last_updated WHERE id = :id;")
            .bind("id", customer.getId())
            .bind("name", customer.getName())
            .bind("created", customer.getCreatedDate())
            .bind("last_updated", customer.getLastUpdatedDate())
            .then()
            .thenReturn(customer)
            ;
    }

    public Mono<Customer> findById(Integer integer) {
        return databaseClient
            .sql("SELECT * FROM customers WHERE id = :id;")
            .bind("id", integer)
            .map(row -> Customer.builder()
                .id(row.get("id", Integer.class))
                .name(row.get("name", String.class))
                .createdDate(ZonedDateTime.of(row.get("created_date", LocalDateTime.class), ZoneOffset.UTC))
                .lastUpdatedDate(row.get("last_updated_date", LocalDateTime.class) != null ? ZonedDateTime.of(row.get("last_updated_date", LocalDateTime.class), ZoneOffset.UTC) : null)
                .build())
            .first()
            ;
    }

    public Flux<Customer> findAll() {
        return databaseClient
            .sql("SELECT * FROM customers;")
            .map(row -> Customer.builder()
                .id(row.get("id", Integer.class))
                .name(row.get("name", String.class))
                .createdDate(ZonedDateTime.of(row.get("created_date", LocalDateTime.class), ZoneOffset.UTC))
                .lastUpdatedDate(row.get("last_updated_date", LocalDateTime.class) != null ? ZonedDateTime.of(row.get("last_updated_date", LocalDateTime.class), ZoneOffset.UTC) : null)
                .build())
            .all()
            ;
    }

    public Mono<Void> deleteById(Integer integer) {
        return databaseClient
            .sql("DELETE FROM customers WHERE id = :id;")
            .bind("id", integer)
            .then()
            ;
    }
}
