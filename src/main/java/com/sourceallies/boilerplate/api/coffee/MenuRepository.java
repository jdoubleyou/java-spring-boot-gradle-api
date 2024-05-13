package com.sourceallies.boilerplate.api.coffee;

import com.sourceallies.boilerplate.api.coffee.entities.Menu;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.*;

@Repository
public class MenuRepository {
    DatabaseClient databaseClient;

    public MenuRepository(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    public Mono<Menu> create(Menu menu) {
        return databaseClient
            .sql("INSERT INTO menus (id, name, created_date, last_updated_date) VALUES (nextval('menus_id_seq'), :name, :created, null) RETURNING id;")
            .bind("name", menu.getName())
            .bind("created", menu.getCreatedDate())
            .map(row -> menu.toBuilder()
                .id(row.get("id", Integer.class))
                .build())
            .first()
            ;
    }

    public Mono<Menu> update(Menu menu) {
        return databaseClient
            .sql("UPDATE menus SET name = :name, created_date = :created, last_updated_date = :last_updated WHERE id = :id;")
            .bind("id", menu.getId())
            .bind("name", menu.getName())
            .bind("created", menu.getCreatedDate())
            .bind("last_updated", menu.getLastUpdatedDate())
            .then()
            .thenReturn(menu)
            ;
    }

    public Mono<Menu> findById(Integer integer) {
        return databaseClient
            .sql("SELECT * FROM menus WHERE id = :id;")
            .bind("id", integer)
            .map(row -> Menu.builder()
                .id(row.get("id", Integer.class))
                .name(row.get("name", String.class))
                .createdDate(ZonedDateTime.of(row.get("created_date", LocalDateTime.class), ZoneOffset.UTC))
                .lastUpdatedDate(row.get("last_updated_date", LocalDateTime.class) != null ? ZonedDateTime.of(row.get("last_updated_date", LocalDateTime.class), ZoneOffset.UTC) : null)
                .build())
            .first()
            ;
    }

    public Flux<Menu> findAll() {
        return databaseClient
            .sql("SELECT * FROM menus;")
            .map(row -> Menu.builder()
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
            .sql("DELETE FROM menus WHERE id = :id;")
            .bind("id", integer)
            .then()
            ;
    }
}
