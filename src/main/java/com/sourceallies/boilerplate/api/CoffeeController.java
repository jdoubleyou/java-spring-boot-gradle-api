package com.sourceallies.boilerplate.api;

import com.sourceallies.boilerplate.api.coffee.*;
import com.sourceallies.boilerplate.api.coffee.entities.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public Iterable<Menu> getMenus() {
        return menuService.getAll();
    }

    @PostMapping(MENUS)
    public ResponseEntity<Void> createMenu(
        @RequestBody @Valid CreateMenuRequest body
    ) {
        return ResponseEntity
            .created(URI.create(MENUS + "/%s"
                .formatted(menuService.create(body).getId())))
            .build();
    }

    @GetMapping(value = MENU_BY_ID)
    public Menu getMenuById(@PathVariable Integer menuId) {
        return menuService.getByIdOrThrow(menuId);
    }

    @PutMapping(MENU_BY_ID)
    public ResponseEntity<Void> updateMenu(
        @PathVariable Integer menuId,
        @RequestBody @Valid UpdateMenuRequest body
    ) {
        menuService.update(menuId, body);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(MENU_BY_ID)
    public ResponseEntity<Void> deleteMenu(
        @PathVariable Integer menuId
    ) {
        menuService.delete(menuId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(CUSTOMERS)
    public Iterable<Customer> getAllCustomers() {
        return customerService.getAll();
    }

    @PostMapping(CUSTOMERS)
    public ResponseEntity<Void> createCustomer(@RequestBody @Valid CreateCustomerRequest body) {
        return ResponseEntity
            .created(URI.create(CUSTOMERS + "/%s"
                .formatted(customerService.create(body).getId())))
            .build();
    }

    @GetMapping(CUSTOMER_BY_ID)
    public Customer getCustomerById(@PathVariable Integer customerId) {
        return customerService.getByIdOrThrow(customerId);
    }

    @PutMapping(CUSTOMER_BY_ID)
    public ResponseEntity<Void> updateCustomerById(
        @PathVariable Integer customerId,
        @RequestBody @Valid UpdateCustomerRequest body
    ) {
        customerService.update(customerId, body);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(CUSTOMER_BY_ID)
    public ResponseEntity<Void> deleteCustomerById(@PathVariable Integer customerId) {
        customerService.delete(customerId);
        return ResponseEntity.noContent().build();
    }
}
