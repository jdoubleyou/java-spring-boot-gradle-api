package com.sourceallies.boilerplate.api.coffee;

import com.sourceallies.boilerplate.api.coffee.entities.CreateMenuRequest;
import com.sourceallies.boilerplate.api.coffee.entities.UpdateMenuRequest;
import com.sourceallies.boilerplate.api.coffee.entities.Menu;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Service
public class MenuService {
    private final MenuRepository menuRepository;

    public MenuService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    public Flux<Menu> getAll() {
        return menuRepository.findAll();
    }

    public Mono<Menu> getById(Integer authorId) {
        return menuRepository
            .findById(authorId)
            .switchIfEmpty(Mono.error(new MenuNotFoundException(authorId)));
    }

    public Mono<Menu> create(CreateMenuRequest request) {
        var menu = Menu.builder()
            .name(request.getName())
            .createdDate(ZonedDateTime.now(ZoneOffset.UTC))
            .build();
        return menuRepository.create(menu);
    }

    public Mono<Menu> update(Integer authorId, UpdateMenuRequest request) {
        return menuRepository
            .findById(authorId)
            .switchIfEmpty(Mono.error(new MenuNotFoundException(authorId)))
            .flatMap(menu -> {
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
                return menuRepository.update(menu);
            });
    }

    public Mono<Void> delete(Integer authorId) {
        return menuRepository.deleteById(authorId);
    }
}
