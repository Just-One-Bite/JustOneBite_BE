package com.delivery.justonebite.shop.application.service;

import com.delivery.justonebite.shop.domain.entity.Category;
import com.delivery.justonebite.shop.domain.entity.Shop;
import com.delivery.justonebite.shop.domain.repository.CategoryRepository;
import com.delivery.justonebite.shop.domain.repository.ShopRepository;
import com.delivery.justonebite.shop.presentation.dto.request.ShopCreateRequest;
import com.delivery.justonebite.shop.presentation.dto.request.ShopSearchRequest;
import com.delivery.justonebite.shop.presentation.dto.response.ShopSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
        Shop shop = request.toEntity(userId);

        List<String> categoryNames = request.categories();
        if (categoryNames != null && !categoryNames.isEmpty()) {
            //기존 카테고리 조회 (한 번에 IN 쿼리)
            Map<String, Category> existingByName = categoryRepository
                    .findAllByCategoryNameIn(categoryNames).stream()
                    .collect(Collectors.toMap(Category::getCategoryName, c -> c));

            //없는 카테고리는 새로 생성
            for (String categoryName : categoryNames) {
                Category category = existingByName.get(categoryName);
                if (category == null) {
                    category = categoryRepository.save(
                            Category.builder()
                                    .categoryName(categoryName)
                                    .build()
                    );
                    existingByName.put(categoryName, category); // 새로 추가된 카테고리도 Map에 넣기
                }

                //ShopCategory 관계 추가
                shop.addCategory(category);
            }
        }

        return shopRepository.save(shop);
    }


}
