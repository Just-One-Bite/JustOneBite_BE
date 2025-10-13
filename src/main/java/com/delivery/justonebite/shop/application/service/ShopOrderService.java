package com.delivery.justonebite.shop.application.service;

import com.delivery.justonebite.global.exception.custom.CustomException;
import com.delivery.justonebite.global.exception.response.ErrorCode;
import com.delivery.justonebite.order.domain.entity.Order;
import com.delivery.justonebite.order.domain.entity.OrderHistory;
import com.delivery.justonebite.order.domain.entity.OrderItem;
import com.delivery.justonebite.order.domain.repository.OrderHistoryRepository;
import com.delivery.justonebite.order.domain.repository.OrderItemRepository;
import com.delivery.justonebite.order.domain.repository.OrderRepository;

import com.delivery.justonebite.shop.domain.entity.Shop;
import com.delivery.justonebite.shop.domain.repository.ShopRepository;
import com.delivery.justonebite.shop.presentation.dto.response.ShopOrderResponse;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShopOrderService {

    private final ShopRepository shopRepository;
    private final OrderRepository orderRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final OrderItemRepository orderItemRepository;

    // 가게별 주문 목록 조회
    @Transactional(readOnly = true)
    public ShopOrderResponse getOrdersByShop(UUID shopId, User user, int page, int size, String sortBy) {
        validateOwner(user);

        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        if (!shop.getOwnerId().equals(user.getId())) {
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

    //권한 검사
    private void validateOwner(User user) {
        if (user == null) {
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }
        if (!user.getUserRole().equals(UserRole.OWNER)) {
            throw new CustomException(ErrorCode.INVALID_USER_ROLE);
        }
    }

}
