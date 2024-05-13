package com.sourceallies.boilerplate.api;

import com.sourceallies.boilerplate.api.coffee.*;
import com.sourceallies.boilerplate.api.coffee.entities.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
public class CoffeeController {
    private static final String MENUS = "/menus";
    private static final String MENU_BY_ID = MENUS + "/{menuId}";

    private static final String CUSTOMERS = "/customers";
    private static final String CUSTOMER_BY_ID = CUSTOMERS + "/{customerId}";

    private final MenuService menuService;
    private final CustomerService customerService;

    public CoffeeController(MenuService menuService, CustomerService customerService) {
        this.menuService = menuService;
        this.customerService = customerService;
    }

    @GetMapping(MENUS)
    public Flux<Menu> getMenus() {
        return menuService.getAll();
    }

    @PostMapping(MENUS)
    public Mono<ResponseEntity<Void>> createMenu(
        @RequestBody @Valid CreateMenuRequest body
    ) {
        return menuService
            .create(body)
            .map(menu -> ResponseEntity
                .created(URI.create(MENUS + "/%s"
                    .formatted(menu.getId())))
                .build());
    }

    @GetMapping(value = MENU_BY_ID)
    public Mono<Menu> getMenuById(@PathVariable Integer menuId) {
        return menuService.getById(menuId);
    }

    @PutMapping(MENU_BY_ID)
    public Mono<ResponseEntity<Void>> updateMenu(
        @PathVariable Integer menuId,
        @RequestBody @Valid UpdateMenuRequest body
    ) {
        return menuService
            .update(menuId, body)
            .map(unused -> ResponseEntity.noContent().build());
    }

    @DeleteMapping(MENU_BY_ID)
    public Mono<ResponseEntity<Void>> deleteMenu(
        @PathVariable Integer menuId
    ) {
        return menuService
            .delete(menuId)
            .thenReturn(ResponseEntity.noContent().build());
    }

    @GetMapping(CUSTOMERS)
    public Flux<Customer> getAllCustomers() {
        return customerService.getAll();
    }

    @PostMapping(CUSTOMERS)
    public Mono<ResponseEntity<Void>> createCustomer(@RequestBody @Valid CreateCustomerRequest body) {
        return customerService
            .create(body)
            .map(customer -> ResponseEntity
                .created(URI.create(CUSTOMERS + "/%s".formatted(customer.getId())))
                .build()
            );
    }

    @GetMapping(CUSTOMER_BY_ID)
    public Mono<Customer> getCustomerById(@PathVariable Integer customerId) {
        return customerService.getById(customerId);
    }

    @PutMapping(CUSTOMER_BY_ID)
    public Mono<ResponseEntity<Void>> updateCustomerById(
        @PathVariable Integer customerId,
        @RequestBody @Valid UpdateCustomerRequest body
    ) {
        return customerService
            .update(customerId, body)
            .map(unused -> ResponseEntity.noContent().build());
    }

    @DeleteMapping(CUSTOMER_BY_ID)
    public Mono<ResponseEntity<Void>> deleteCustomerById(@PathVariable Integer customerId) {
        return customerService
            .delete(customerId)
            .thenReturn(ResponseEntity.noContent().build());
    }
}
