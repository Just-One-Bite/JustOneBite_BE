package com.delivery.justonebite.order.application.service;

import com.delivery.justonebite.global.exception.custom.CustomException;
import com.delivery.justonebite.global.exception.response.ErrorCode;
import com.delivery.justonebite.item.domain.entity.Item;
import com.delivery.justonebite.item.domain.repository.ItemRepository;
import com.delivery.justonebite.order.domain.entity.Order;
import com.delivery.justonebite.order.domain.entity.OrderHistory;
import com.delivery.justonebite.order.domain.entity.OrderItem;
import com.delivery.justonebite.order.domain.enums.OrderStatus;
import com.delivery.justonebite.order.domain.repository.OrderHistoryRepository;
import com.delivery.justonebite.order.domain.repository.OrderItemRepository;
import com.delivery.justonebite.order.domain.repository.OrderRepository;
import com.delivery.justonebite.order.presentation.dto.OrderItemDto;
import com.delivery.justonebite.order.presentation.dto.request.CreateOrderRequest;
import com.delivery.justonebite.order.presentation.dto.request.UpdateOrderStatusRequest;
import com.delivery.justonebite.order.presentation.dto.response.CustomerOrderResponse;
import com.delivery.justonebite.order.presentation.dto.response.GetOrderStatusResponse;
import com.delivery.justonebite.order.presentation.dto.response.GetOrderStatusResponse.OrderHistoryDto;
import com.delivery.justonebite.order.presentation.dto.response.OrderDetailsResponse;
import com.delivery.justonebite.user.domain.entity.User;
import com.delivery.justonebite.user.domain.entity.UserRole;
import com.delivery.justonebite.user.domain.repository.UserRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    private final OrderHistoryRepository orderHistoryRepository;
    private final ItemRepository itemRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;

    // TODO: User, Shop 정보 받아와야 함 (추후 더미 데이터 수정 필요)
    @Transactional
    public void createOrder(CreateOrderRequest request) {

        // TODO: Address 테이블에서 Id로 주소값 가져와야 함 (없으면 예외처리)
        String address = "서울시 종로구 사직로 155-2";

        List<UUID> itemIds = request.orderItems().stream()
            .map(OrderItemDto::itemId)
            .toList();

        List<Item> foundItems = itemRepository.findAllByItemIdIn(itemIds);

        // 상품 개수 검증
        if (foundItems.size() != itemIds.size()) {
            // 요청된 상품의 itemId 중 유효하지 않은게 있을 경우
            throw new CustomException(ErrorCode.INVALID_ITEM);
        }

        // Map으로 변환하여 조회 속도 개선
        Map<UUID, Item> itemMap = foundItems.stream()
            .collect(Collectors.toMap(Item::getItemId, item -> item));

        // Item별 가격과 count 곱하여 총 금액 계산
        Integer totalPrice = request.orderItems().stream()
            .mapToInt(dto -> itemMap.get(dto.itemId()).getPrice() * dto.count())
            .sum();

        // 추후에 created_by에 userId 추가
        Order order = Order.create(address,
                request.userPhoneNumber(),
                convertToOrderName(request, itemMap),
                totalPrice,
                request.orderRequest(),
                request.deliveryRequest()
            );

        orderRepository.save(order);

        // OrderItem 생성 및 여러 주문 아이템 한번에 저장 (트랜잭션 최적화)s
        List<OrderItem> orderItems = request.orderItems().stream()
            .map(dto -> {
                Item item = itemMap.get(dto.itemId());
                return OrderItem.create(order, item, dto.count());
            })
            .toList();

        orderItemRepository.saveAll(orderItems);

        // 주문 내역 저장
        orderHistoryRepository.save(OrderHistory.create(order, OrderStatus.PENDING));
    }

    @Transactional(readOnly = true)
    public Page<CustomerOrderResponse> getCustomerOrders(int page, int size, String sortBy) {
        // TODO: USE ROLE이 CUSTOMER일 경우에만 조회 가능하도록 처리 필요
//        UserRoleEnum role = user.getRole();

        // 주문 목록은 기본적으로 내림차순으로 표시됨
        Sort.Direction dir = Direction.DESC;
        Sort sort = Sort.by(dir, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        return orderRepository.findAll(pageable)
            .map(order -> CustomerOrderResponse.toDto(order, getOrderStatus(order)));
    }

    @Transactional(readOnly = true)
    public OrderDetailsResponse getOrderDetails(UUID orderId, User user) {
        authorizeUser(user);

        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));

        List<OrderItemDto> orderItems = orderItemRepository
            .findAllByOrder(order)
            .stream()
            .map(OrderItemDto::from)
            .toList();
        return OrderDetailsResponse.toDto(order, orderItems);
    }

    private OrderStatus getOrderStatus(Order order) {
        return orderHistoryRepository.findByOrderId(order.getId())
            .map(OrderHistory::getStatus)
            .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    private String convertToOrderName(CreateOrderRequest request, Map<UUID, Item> itemMap) {
        String firstItem = itemMap.get(request.orderItems().getFirst().itemId()).getName();
        int count = request.orderItems().size() - 1;
        return count > 0 ? firstItem + " 외 " + count + "건" : firstItem;
    }

    @Transactional
    public void updateOrderStatus(UUID orderId, UpdateOrderStatusRequest request, User user) {
        // 유저 Role 권한 검증 : 가게 주인(OWNER)만 가능
        authorizeOwner(user);

        // 주문 엔티티 조회 및 검증 (상태 전이 유효성 검사 포함)
        Order order = this.getValidatedOrder(orderId, request.newStatus());

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

    private void authorizeOwner(User user) {
        authorizeUser(user);
        if (!(user.getUserRole().equals(UserRole.OWNER))) {
            throw new CustomException(ErrorCode.INVALID_USER_ROLE);
        }
    }

    private Order getValidatedOrder(UUID orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        // TODO: 추후에 develop 머지 된걸로 수정 예정 (추후, Order Status 필드 Order 엔티티로 이동)
        OrderHistory orderHistory = orderHistoryRepository.findByOrderId(orderId)
            .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        OrderStatus currentStatus = OrderStatus.of(orderHistory.getStatus().name());
        OrderStatus nextStatus = OrderStatus.of(newStatus);

        // 주문 상태 전이 유효성 검증
        if (!currentStatus.isValidNextStatus(nextStatus)) {
            throw new CustomException(ErrorCode.INVALID_ORDER_STATUS);
        }
        return order;
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
            return orderRepository.existsByIdAndShop_Owner(orderId, user.getId());
        }
        return false;
    }
}
