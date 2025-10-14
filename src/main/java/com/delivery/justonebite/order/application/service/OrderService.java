package com.delivery.justonebite.order.application.service;

import com.delivery.justonebite.global.exception.custom.CustomException;
import com.delivery.justonebite.global.exception.response.ErrorCode;
import com.delivery.justonebite.item.domain.entity.Item;
import com.delivery.justonebite.item.domain.repository.ItemRepository;
import com.delivery.justonebite.order.domain.entity.Order;
import com.delivery.justonebite.order.domain.entity.OrderHistory;
import com.delivery.justonebite.order.domain.entity.OrderItem;
import com.delivery.justonebite.order.domain.enums.OrderStatus;
import com.delivery.justonebite.order.domain.factory.OrderFactory;
import com.delivery.justonebite.order.domain.repository.OrderHistoryRepository;
import com.delivery.justonebite.order.domain.repository.OrderItemRepository;
import com.delivery.justonebite.order.domain.repository.OrderRepository;
import com.delivery.justonebite.order.presentation.dto.OrderItemDto;
import com.delivery.justonebite.order.presentation.dto.request.CancelOrderRequest;
import com.delivery.justonebite.order.presentation.dto.request.CreateOrderRequest;
import com.delivery.justonebite.order.presentation.dto.request.UpdateOrderStatusRequest;
import com.delivery.justonebite.order.presentation.dto.response.CustomerOrderResponse;
import com.delivery.justonebite.order.presentation.dto.response.GetOrderStatusResponse;
import com.delivery.justonebite.order.presentation.dto.response.OrderCancelResponse;
import com.delivery.justonebite.order.presentation.dto.response.OrderDetailsResponse;
import com.delivery.justonebite.user.domain.entity.Address;
import com.delivery.justonebite.order.presentation.dto.response.OrderDetailsResponse.ShopInfoDto;
import com.delivery.justonebite.user.domain.entity.User;
import com.delivery.justonebite.user.domain.entity.UserRole;
import com.delivery.justonebite.user.domain.repository.AddressRepository;
import com.delivery.justonebite.user.domain.repository.UserRepository;
import com.delivery.justonebite.shop.domain.entity.Shop;
import com.delivery.justonebite.shop.domain.repository.ShopRepository;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ShopRepository shopRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final ItemRepository itemRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final OrderFactory orderFactory;

    @Transactional
    public void createOrder(CreateOrderRequest request, User user) {
        // 유저 Role 권한 검증
        authorizeCustomer(user);

        // TODO: DUMMY 데이터 (고객 Address 등록 기능 개발 이후 삭제 예정)
        String address = "서울시 종로구 사직로 155-2";
//        Address address = addressRepository.findByUser_IdAndIsDefaultTrue(user.getId())
//            .orElseThrow(() -> new CustomException(ErrorCode.ADDRESS_NOT_FOUND));

        List<Item> validatedItems = getValidatedItems(request);

        // Map으로 변환하여 조회 속도 개선
        Map<UUID, Item> itemMap = validatedItems.stream()
            .collect(Collectors.toMap(Item::getItemId, item -> item));

        Shop shop = shopRepository.findById(request.shopId())
            .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));

        // OrderFactory에서 총 금액 계산 및 Order 객체 생성, OrderItem 객체 생성 모두 처리
        Order order = orderFactory.create(user, shop, address, request, itemMap);
        // 총 금액 검증
        validateOrderTotalPrice(request, order);

        orderRepository.save(order);

        // OrderFactory가 생성한 OrderItem 리스트를 가져와 저장
        orderItemRepository.saveAll(orderFactory.getOrderItems(order, request.orderItems(), itemMap));

        // 주문 내역 저장
        orderHistoryRepository.save(OrderHistory.create(order, OrderStatus.PENDING));
    }

    @Transactional(readOnly = true)
    public Page<CustomerOrderResponse> getCustomerOrders(int page, int size, String sortBy, User user) {
        authorizeCustomer(user);

        // 주문 목록은 기본적으로 내림차순으로 표시됨
        Sort.Direction dir = Direction.DESC;
        Sort sort = Sort.by(dir, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        return orderRepository.findAll(pageable)
            .map(CustomerOrderResponse::toDto);
    }

    @Transactional(readOnly = true)
    public OrderDetailsResponse getOrderDetails(UUID orderId, User user) {
        authorizeUser(user);

        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        List<OrderItemDto> orderItems = orderItemRepository
            .findAllByOrder(order)
            .stream()
            .map(OrderItemDto::from)
            .toList();

        ShopInfoDto dto = ShopInfoDto.toDto(order.getShop().getId(), order.getShop().getName());
        return OrderDetailsResponse.toDto(order, dto, orderItems);
    }

    private void authorizeCustomer(User user) {
        if (!(user.getUserRole().equals(UserRole.CUSTOMER))) {
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }
    }

    @Transactional
    public void updateOrderStatus(UUID orderId, UpdateOrderStatusRequest request, User user) {
        // 유저 Role 권한 검증 : 가게 주인(OWNER)만 가능
        authorizeOwner(user);

        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        order.updateCurrentStatus(OrderStatus.of(request.newStatus()));

        // OrderHistory 엔티티에는 새로운 상태와 함께 생성 시간이 기록되어야 함 (점이력)
        orderHistoryRepository.save(OrderHistory.create(order, OrderStatus.of(request.newStatus())));
    }

    @Transactional(readOnly = true)
    public GetOrderStatusResponse getOrderStatusHistories(UUID orderId, User user) {
        authorizeUser(user);
        if (!isAuthorizedUserRole(orderId, user)) { throw new CustomException(ErrorCode.FORBIDDEN_ACCESS); }

        // 주문에 해당하는 주문 상태 기록 내역을 최신 순으로 정렬
        List<OrderHistory> histories = orderHistoryRepository.findAllByOrder_IdOrderByCreatedAtDesc(orderId);

        if (histories.isEmpty()) {
            throw new CustomException(ErrorCode.ORDER_STATUS_NOT_FOUND);
        }

        return GetOrderStatusResponse.toDto(orderId, histories);
    }

    @Transactional
    public OrderCancelResponse cancelOrder(CancelOrderRequest request, UUID orderId, User user) {
        // 취소 요청만 허용
        if (!OrderStatus.ORDER_CANCELLED.name().equals(request.status())) {
            throw new CustomException(ErrorCode.INVALID_CANCEL_STATUS_VALUE);
        }

        authorizeCustomer(user);

        Order order = orderRepository.findByIdWithCustomer(orderId)
            .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        // 현재 로그인한 사용자가 주문자와 동일하지 않을 경우
        if (!user.getId().equals(order.getCustomer().getId())) {
            throw new CustomException(ErrorCode.ORDER_USER_NOT_MATCH);
        }

        // Order의 currentStatus 업데이트 (모든 비즈니스 규칙 위임)
        order.updateCurrentStatus(OrderStatus.ORDER_CANCELLED);

        // 트랜잭션 종료 시점 변경 내용 반영 (상태 동기화)
        orderHistoryRepository.save(OrderHistory.create(order, OrderStatus.ORDER_CANCELLED));

        return OrderCancelResponse.toDto(order, LocalDateTime.now());
    }

    private void authorizeOwner(User user) {
        authorizeUser(user);
        if (!(user.getUserRole().equals(UserRole.OWNER))) {
            throw new CustomException(ErrorCode.INVALID_USER_ROLE);
        }
    }

    private List<Item> getValidatedItems(CreateOrderRequest request) {
        // 유효한 아이템 객체들만 반환
        List<UUID> itemIds = request.orderItems().stream()
            .map(OrderItemDto::itemId)
            .toList();

        List<Item> foundItems = itemRepository.findAllByItemIdIn(itemIds);

        // 상품 개수 검증
        if (foundItems.size() != itemIds.size()) {
            // 요청된 상품의 itemId 중 유효하지 않은게 있을 경우
            throw new CustomException(ErrorCode.INVALID_ITEM);
        }
        return foundItems;
    }

    private void validateOrderTotalPrice(CreateOrderRequest request, Order order) {
        // 클라이언트에서 받아온 총 금액과 실제 상품 금액을 다 합한 총 금액이 같은지 검증
        if (!request.totalPrice().equals(order.getTotalPrice())) {
            throw new CustomException(ErrorCode.TOTAL_PRICE_NOT_MATCH);
        }
    }

    private void authorizeUser(User user) {
        // DB에 User로 존재하는지 유효성 검사
        userRepository.findById(user.getId())
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private boolean isAuthorizedUserRole(UUID orderId, User user) {
        // 유저 접근 권한 확인
        UserRole userRole = user.getUserRole();
        if (userRole.isAdmin()) {
            return true;
        }
        if (userRole.isCustomer()) {
            return orderRepository.existsByIdAndCustomer_Id(orderId, user.getId());
        }
        if (userRole.isOwner()) {
            return orderRepository.existsByIdAndShop_OwnerId(orderId, user.getId());
        }
        return false;
    }
}
