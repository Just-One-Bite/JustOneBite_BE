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

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;
    private final CategoryRepository categoryRepository;

    private final OrderRepository orderRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final OrderItemRepository orderItemRepository;


    // 가게 등록
    @Transactional
    public Shop createShop(ShopCreateRequest request, Long userId, UserRole role) {
        // 권한 체크
        if (role != UserRole.OWNER) {
            throw new CustomException(ErrorCode.INVALID_USER_ROLE);
        }

        Shop shop = request.toEntity(userId);

        List<String> categoryNames = request.categories();
        if (categoryNames != null && !categoryNames.isEmpty()) {

            // DB에 존재하는 카테고리만 조회 (없는 건 예외)
            List<Category> foundCategories = categoryRepository.findAllByCategoryNameIn(categoryNames);

            // 요청 중 실제 존재하지 않는 카테고리명 필터링
            Set<String> foundNames = foundCategories.stream()
                    .map(Category::getCategoryName)
                    .collect(Collectors.toSet());

            List<String> notFound = categoryNames.stream()
                    .filter(name -> !foundNames.contains(name))
                    .toList();

            if (!notFound.isEmpty()) {
                throw new CustomException(ErrorCode.CATEGORY_NOT_FOUND);
            }

            // ShopCategory 관계 추가
            for (Category category : foundCategories) {
                shop.addCategory(category); // 중복 방지 로직 포함
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

        if (role == UserRole.OWNER && !shop.getOwnerId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_SHOP_ACCESS);
        }

        // 필드 업데이트
        shop.updateInfo(
                request.name(),
                request.phone_number(),
                request.operating_hour(),
                request.description()
        );

        // 카테고리 수정
        if (request.categories() != null) {
            Set<String> currentNames = shop.getCategories().stream()
                    .map(sc -> sc.getCategory().getCategoryName())
                    .collect(Collectors.toSet());

            Set<String> newNames = new HashSet<>(request.categories());

            // 아무 변화도 없으면 skip
            if (!currentNames.equals(newNames)) {

                // 삭제할 카테고리: 현재엔 있지만 새 목록엔 없는 것
                Set<String> toRemove = currentNames.stream()
                        .filter(name -> !newNames.contains(name))
                        .collect(Collectors.toSet());

                // 추가할 카테고리: 새 목록엔 있지만 현재엔 없는 것
                Set<String> toAdd = newNames.stream()
                        .filter(name -> !currentNames.contains(name))
                        .collect(Collectors.toSet());

                // 삭제
                shop.getCategories().removeIf(sc -> toRemove.contains(sc.getCategory().getCategoryName()));

                if (!toAdd.isEmpty()) {
                    List<Category> found = categoryRepository.findAllByCategoryNameIn(new ArrayList<>(toAdd));

                    // DB에 존재하는 카테고리 이름 set
                    Set<String> foundNames = found.stream()
                            .map(Category::getCategoryName)
                            .collect(Collectors.toSet());

                    // 존재하지 않는 이름
                    List<String> notFound = toAdd.stream()
                            .filter(name -> !foundNames.contains(name))
                            .toList();

                    // 없는 카테고리는 새로 생성
                    for (String name : notFound) {
                        Category newCategory = categoryRepository.save(Category.builder()
                                .categoryName(name)
                                .build());
                        found.add(newCategory);
                    }

                    // ShopCategory 추가
                    for (Category category : found) {
                        shop.getCategories().add(
                                ShopCategory.builder()
                                        .shop(shop)
                                        .category(category)
                                        .build()
                        );
                    }
                }
            }
        }

            return new ShopUpdateResponse(shop.getUpdatedAt(), shop.getUpdatedBy());

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
