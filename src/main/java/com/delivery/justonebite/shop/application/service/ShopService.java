package com.delivery.justonebite.shop.application.service;

import com.delivery.justonebite.global.exception.custom.CustomException;
import com.delivery.justonebite.global.exception.response.ErrorCode;
import com.delivery.justonebite.order.domain.entity.Order;
import com.delivery.justonebite.order.domain.entity.OrderHistory;
import com.delivery.justonebite.order.domain.entity.OrderItem;
import com.delivery.justonebite.order.domain.enums.OrderStatus;
import com.delivery.justonebite.order.domain.repository.OrderHistoryRepository;
import com.delivery.justonebite.order.domain.repository.OrderItemRepository;
import com.delivery.justonebite.order.domain.repository.OrderRepository;
import com.delivery.justonebite.shop.domain.entity.Category;
import com.delivery.justonebite.shop.domain.entity.RejectStatus;
import com.delivery.justonebite.shop.domain.entity.Shop;
import com.delivery.justonebite.shop.domain.entity.ShopCategory;
import com.delivery.justonebite.shop.domain.repository.CategoryRepository;
import com.delivery.justonebite.shop.domain.repository.ShopRepository;
import com.delivery.justonebite.shop.presentation.dto.request.ShopCreateRequest;
import com.delivery.justonebite.shop.presentation.dto.request.ShopUpdateRequest;
import com.delivery.justonebite.shop.presentation.dto.response.ShopDeleteResponse;
import com.delivery.justonebite.shop.presentation.dto.response.ShopOrderResponse;
import com.delivery.justonebite.shop.presentation.dto.response.ShopUpdateResponse;
import com.delivery.justonebite.user.domain.entity.User;
import com.delivery.justonebite.user.domain.entity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    private final OrderRepository orderRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final OrderItemRepository orderItemRepository;


    //가게 등록
    @Transactional
    public Shop createShop(ShopCreateRequest request, Long userId, UserRole role) {
        //권한 체크
        if (role != UserRole.CUSTOMER) {
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
    public ShopUpdateResponse updateShop(ShopUpdateRequest request, UUID shopId, Long userId, UserRole role) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new CustomException(ErrorCode.SHOP_NOT_FOUND));
        // 권한 체크
        if (!(role == UserRole.OWNER || role == UserRole.MASTER || role == UserRole.MANAGER)) {
            throw new CustomException(ErrorCode.INVALID_USER_ROLE);
        }
        // OWNER 본인 가게 여부 확인
        if (role == UserRole.OWNER && !shop.getOwnerId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_SHOP_ACCESS);
        }

        // 1. 변경된 필드 수정
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
                        .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
                ShopCategory sc = ShopCategory.builder().shop(shop).category(category).build();
                shop.getCategories().add(sc);
            }
        }
        return new ShopUpdateResponse(shop.getUpdatedAt(),shop.getUpdatedBy());
    }

    //가게 삭제
    @Transactional
    public ShopDeleteResponse deleteShop(UUID shopId, Long userId, UserRole userRole){
        //가게 존재 확인
        Shop shop = shopRepository.findByIdAndDeletedAtIsNull(shopId)
           .orElseThrow(()-> new CustomException(ErrorCode.SHOP_NOT_FOUND));

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
        //  주문 상태가 COMPLETED가 아니면 삭제 불가
        boolean notCompletedOrder = orderHistoryRepository.existsByOrder_Shop_IdAndStatusNot(shopId, OrderStatus.COMPLETED);
        if(notCompletedOrder) {
            throw new CustomException(ErrorCode.NOT_COMPLETED_ORDER_EXISTS);
        }

        //soft Delete
        shop.markDeleted(userId);

        // 관리자에게 삭제 요청 상태로 변경
        shop.requestDelete();

        return new ShopDeleteResponse(shop.getDeletedAt(), shop.getDeleteAcceptStatus());

    }

    // 가게별 주문 목록 조회
    @Transactional(readOnly = true)
    public ShopOrderResponse getOrdersByShop(UUID shopId, User user, int page, int size, String sortBy) {

        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        //Role이 Owner인 경우 본인 가게만 접근 가능하도록 추가 검증
        if (user.getUserRole().equals(UserRole.OWNER) && !shop.getOwnerId().equals(user.getId())) {
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortBy));
        Page<Order> orders = orderRepository.findAllByShop_Id(shopId, pageable);

        Page<ShopOrderResponse.OrderSummary> orderSummaries = orders.map(order -> {
            OrderHistory latestHistory = orderHistoryRepository
                    .findTopByOrder_IdOrderByCreatedAtDesc(order.getId())
                    .orElseThrow(() -> new CustomException(ErrorCode.ORDER_STATUS_NOT_FOUND));

            List<OrderItem> orderItems = orderItemRepository.findAllByOrder(order);
            List<String> itemNames = orderItems.stream()
                    .map(oi -> oi.getItem().getName())
                    .toList();

            return ShopOrderResponse.OrderSummary.of(order, latestHistory.getStatus(), itemNames);
        });

        return ShopOrderResponse.from(orderSummaries);
    }



}
