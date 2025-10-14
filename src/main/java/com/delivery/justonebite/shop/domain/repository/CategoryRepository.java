package com.delivery.justonebite.shop.domain.repository;

import com.delivery.justonebite.shop.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    List<Category> findAllByCategoryNameIn(List<String> categoryNames);

    Optional<Category> findByCategoryName(String categoryName);

}
