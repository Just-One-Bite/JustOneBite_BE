package com.delivery.justonebite.shop.application.service;

import com.delivery.justonebite.shop.domain.entity.Category;
import com.delivery.justonebite.shop.domain.entity.Shop;
import com.delivery.justonebite.shop.domain.repository.CategoryRepository;
import com.delivery.justonebite.shop.domain.repository.ShopRepository;
import com.delivery.justonebite.shop.presentation.dto.request.ShopCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;
    private final CategoryRepository categoryRepository;


    @Transactional
    public void createShop(ShopCreateRequest request) {
        Shop shop = request.toEntity(1L, 1L); // TODO: 실제 ownerId, userId 로 변경

        if (request.categories() != null) {
            request.categories().forEach(categoryName -> {
                
                Category category = categoryRepository.findByCategoryName(categoryName)
                        .orElseGet(() -> categoryRepository.save(
                                Category.builder().categoryName(categoryName).build()
                        ));
                // Shop과 Category 연결
                shop.addCategory(category);
            });
        }

        shopRepository.save(shop);
    }
}
