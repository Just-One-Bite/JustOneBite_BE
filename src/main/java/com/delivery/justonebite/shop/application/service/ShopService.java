package com.delivery.justonebite.shop.application.service;

import com.delivery.justonebite.global.exception.custom.CustomException;
import com.delivery.justonebite.global.exception.response.ErrorCode;
import com.delivery.justonebite.shop.domain.entity.Category;
import com.delivery.justonebite.shop.domain.entity.RejectStatus;
import com.delivery.justonebite.shop.domain.entity.Shop;
import com.delivery.justonebite.shop.domain.entity.ShopCategory;
import com.delivery.justonebite.shop.domain.repository.CategoryRepository;
import com.delivery.justonebite.shop.domain.repository.ShopRepository;
import com.delivery.justonebite.shop.presentation.dto.request.ShopCreateRequest;
import com.delivery.justonebite.shop.presentation.dto.request.ShopUpdateRequest;
import com.delivery.justonebite.shop.presentation.dto.response.ShopDeleteResponse;
import com.delivery.justonebite.user.domain.entity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;
    private final CategoryRepository categoryRepository;
    
    //가게 등록
    @Transactional
    public Shop createShop(ShopCreateRequest request, Long userId, UserRole role) {
        //권한 체크
        if (role != UserRole.OWNER) {
            throw new CustomException(ErrorCode.INVALID_USER_ROLE);
        }

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

    //가게 수정
    @Transactional
    public Shop updateShop(ShopUpdateRequest request, UUID shopId, Long userId, UserRole role) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_SHOP));

        if (!(role == UserRole.OWNER || role == UserRole.MASTER || role == UserRole.MANAGER)) {
            throw new CustomException(ErrorCode.INVALID_USER_ROLE);
        }

        if (role == UserRole.OWNER && !shop.getOwnerId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_SHOP_ACCESS);
        }

        // 1. 기본 필드 수정
        shop.updateInfo(
                request.name(),
                request.phone_number(),
                request.operating_hour(),
                request.description()
        );

        // 2. 카테고리 문자열 → Category 엔티티 변환
        if (request.categories() != null && !request.categories().isEmpty()) {
            shop.getCategories().clear();
            for (String categoryName : request.categories()) {
                Category category = categoryRepository.findByCategoryName(categoryName)
                        .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
                ShopCategory sc = ShopCategory.builder().shop(shop).category(category).build();
                shop.getCategories().add(sc);
            }
        }
        return shop;
    }

    //가게 삭제
    @Transactional
    public ShopDeleteResponse deleteShop(UUID shopId, Long userId, UserRole userRole){
        //가게 존재 확인
        Shop shop = shopRepository.findByIdAndDeletedAtIsNull(shopId)
           .orElseThrow(()-> new CustomException(ErrorCode.INVALID_SHOP));

        //owner 권한 체크
        if (!(userRole == UserRole.OWNER || userRole == UserRole.MANAGER || userRole == UserRole.MASTER)) {
            throw new CustomException(ErrorCode.INVALID_USER_ROLE);
        }

        //본인 가게인지 체크
        if (userRole == UserRole.OWNER && !shop.getOwnerId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_SHOP_ACCESS);
        }

        // 이미 요청 중인지 확인
        if (shop.getDeleteAcceptStatus() == RejectStatus.PENDING) {
            throw new CustomException(ErrorCode.ALREADY_PENDING_DELETE);
        }

        //soft Delete
        shop.markDeleted(userId);

        // 관리자에게 삭제 요청 상태로 변경
        shop.requestDelete();

        return new ShopDeleteResponse(shop.getDeletedAt(), shop.getDeleteAcceptStatus());

    }




}
