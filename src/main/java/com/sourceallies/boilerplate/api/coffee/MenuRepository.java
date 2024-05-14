package com.sourceallies.boilerplate.api.coffee;

import com.sourceallies.boilerplate.api.coffee.entities.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Integer> {
    @Override
    Menu save(Menu menu);

    @Override
    Optional<Menu> findById(Integer id);
}
