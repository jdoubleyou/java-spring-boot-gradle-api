package com.sourceallies.boilerplate.api.coffee;

import com.sourceallies.boilerplate.api.coffee.entities.CreateMenuRequest;
import com.sourceallies.boilerplate.api.coffee.entities.UpdateMenuRequest;
import com.sourceallies.boilerplate.api.coffee.entities.Menu;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Service
public class MenuService {
    private final MenuRepository menuRepository;

    public MenuService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    public Iterable<Menu> getAll() {
        return menuRepository.findAll();
    }

    public Menu getByIdOrThrow(Integer authorId) {
        return menuRepository
            .findById(authorId)
            .orElseThrow(() -> new MenuNotFoundException(authorId));
    }

    public Menu create(CreateMenuRequest request) {
        var menu = Menu.builder()
            .name(request.getName())
            .createdDate(ZonedDateTime.now(ZoneOffset.UTC))
            .build();
        return menuRepository.save(menu);
    }

    public Menu update(Integer authorId, UpdateMenuRequest request) {
        Menu menu = menuRepository.findById(authorId).orElseThrow(() -> new MenuNotFoundException(authorId));
        boolean hasChanges = false;
        if (
            !ObjectUtils.nullSafeEquals(
                menu.getName(),
                request.getName()
            )
        ) {
            menu.setName(request.getName());
            hasChanges = true;
        }

        if (hasChanges) {
            menu.setLastUpdatedDate(ZonedDateTime.now(ZoneOffset.UTC));
        }
        return menuRepository.save(menu);
    }

    public void delete(Integer authorId) {
        menuRepository.deleteById(authorId);
    }
}
