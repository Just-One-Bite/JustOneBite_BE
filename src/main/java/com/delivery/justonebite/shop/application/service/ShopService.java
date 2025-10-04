package com.delivery.justonebite.shop.application.service;

import com.delivery.justonebite.shop.domain.entity.Category;
import com.delivery.justonebite.shop.domain.entity.Shop;
import com.delivery.justonebite.shop.domain.repository.CategoryRepository;
import com.delivery.justonebite.shop.domain.repository.ShopRepository;
import com.delivery.justonebite.shop.presentation.dto.request.ShopCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;
    private final CategoryRepository categoryRepository;

    // TODO: user 권한 체크
    @Transactional
    public Shop createShop(ShopCreateRequest request, Long userId) {
        Shop shop = request.toEntity(userId); // TODO: 실제 ownerId, userId 로 변경

        List<String> categoryNames = request.categories();
        if (categoryNames != null && !categoryNames.isEmpty()) {
            // 기존 카테고리 조회
            Map<String, Category> existingByName = categoryRepository
                    .findAllByCategoryNameIn(categoryNames).stream()
                    .collect(Collectors.toMap(Category::getCategoryName, c -> c));

            // 없는 카테고리는 새로 저장
            for (String categoryName : categoryNames) {
                Category category = existingByName.get(categoryName);
                if (category == null) {
                    category = categoryRepository.save(
                            Category.builder().categoryName(categoryName).build()
                    );
                }
                shop.addCategory(category);
            }
        }

        return shopRepository.save(shop);
    }
}
