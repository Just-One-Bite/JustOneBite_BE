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
import com.delivery.justonebite.payment.application.service.PaymentService;
import com.delivery.justonebite.payment.domain.entity.Payment;
import com.delivery.justonebite.payment.domain.entity.PaymentStatus;
import com.delivery.justonebite.payment.domain.repository.PaymentRepository;
import com.delivery.justonebite.payment.presentation.dto.request.PaymentCancelRequest;
import com.delivery.justonebite.payment.presentation.dto.request.PaymentRequest;
import com.delivery.justonebite.payment.presentation.dto.response.PaymentCancelResponse;
import com.delivery.justonebite.payment.presentation.dto.response.PaymentFailResponse;
import com.delivery.justonebite.payment.presentation.dto.response.PaymentResponse;
import com.delivery.justonebite.payment.presentation.dto.response.PaymentSuccessResponse;
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
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final PaymentService paymentService;
    private final OrderRepository orderRepository;
    private final ShopRepository shopRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final ItemRepository itemRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final AddressRepository addressRepository;
    private final OrderFactory orderFactory;

    @Transactional
    public PaymentResponse createOrder(CreateOrderRequest request, User user) {
        // 유저 Role 권한 검증
        authorizeCustomer(user);

        Address address = addressRepository.findByUser_IdAndIsDefaultTrue(user.getId())
            .orElseThrow(() -> new CustomException(ErrorCode.ADDRESS_NOT_FOUND));

        List<Item> validatedItems = getValidatedItems(request);

        // Map으로 변환하여 조회 속도 개선
        Map<UUID, Item> itemMap = validatedItems.stream()
            .collect(Collectors.toMap(Item::getItemId, item -> item));

        Shop shop = shopRepository.findById(request.shopId())
            .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));

        // OrderFactory에서 총 금액 계산 및 Order 객체 생성, OrderItem 객체 생성 모두 처리
        Order order = orderFactory.create(user, shop, address.getAddress(), request, itemMap);
        // 총 금액 검증
        validateOrderTotalPrice(request, order.getTotalPrice());

        // 주문 저장
        orderRepository.save(order);
        // OrderFactory가 생성한 OrderItem 리스트를 가져와 저장
        orderItemRepository.saveAll(orderFactory.getOrderItems(order, request.orderItems(), itemMap));

        // 결제 요청
        PaymentResponse paymentResponse = requestPayment(order);

        if (paymentResponse instanceof PaymentSuccessResponse) {
            // 결제 요청 성공 시, 주문 상태는 PENDING 유지
            orderHistoryRepository.save(OrderHistory.create(order, OrderStatus.PENDING));
        } else {
            // 결제 요청 실패 시 (카드 거절, 취소)
            orderHistoryRepository.save(OrderHistory.create(order, OrderStatus.ORDER_CANCELLED));
            return new PaymentFailResponse(order.getId(), HttpStatusCode.valueOf(500).toString(), ErrorCode.PAYMENT_REQUEST_FAIL.getDescription());
        }

        return paymentResponse;
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

        // 결제 상태가 완료되어야하만 취소 요청 가능
        Payment payment = paymentService.getPaymentByOrderId(orderId);
        if (!PaymentStatus.DONE.name().equals(payment.getStatus().name())) {
            throw new CustomException(ErrorCode.PAYMENT_STATUS_CANCEL_NOT_ALLOWED);
        }

        Order order = orderRepository.findByIdWithCustomer(orderId)
            .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        // Order에 기록된 총 금액과 취소 요청 금액이 동일한지 확인
        // (고객으로부터 들어오는 취소 요청은 전액 취소로 가정)
        order.validateCancellationTotalPrice(request.cancelAmount());

        // 권한 검증
        authorizeCustomer(user);

        // 현재 로그인한 사용자가 주문자와 동일하지 않을 경우
        if (!user.getId().equals(order.getCustomer().getId())) {
            throw new CustomException(ErrorCode.ORDER_USER_NOT_MATCH);
        }

        // 결제 취소 요청
        PaymentCancelResponse paymentCancelResponse = requestPaymentCancel(request);
        if (!PaymentStatus.CANCELED.name().equals(paymentCancelResponse.status().name())) {
            throw new CustomException(ErrorCode.ORDER_CANCEL_FAILED);
        }

        // Order의 currentStatus 업데이트 (모든 비즈니스 규칙 위임)
        order.updateCurrentStatus(OrderStatus.ORDER_CANCELLED);

        // 트랜잭션 종료 시점 변경 내용 반영 (상태 동기화)
        orderHistoryRepository.save(OrderHistory.create(order, OrderStatus.ORDER_CANCELLED));

        return OrderCancelResponse.toDto(order, LocalDateTime.now());
    }

    private PaymentResponse requestPayment(Order order) {
        PaymentRequest request = PaymentRequest.builder()
            .orderId(order.getId())
            .orderName(order.getOrderName())
            .amount(order.getTotalPrice())
            .status(true)
            .build();
        return paymentService.requestPayment(request);
    }

    private PaymentCancelResponse requestPaymentCancel(CancelOrderRequest request) {
        PaymentCancelRequest cancelRequest = PaymentCancelRequest.builder()
            .paymentKey(request.paymentKey())
            .cancelReason(request.cancelReason())
            .cancelAmount(request.cancelAmount())
            .build();

        return paymentService.cancelPayment(cancelRequest);
    }

    private void authorizeOwner(User user) {
        authorizeUser(user);
        if (!(user.getUserRole().equals(UserRole.OWNER))) {
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
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

    private void validateOrderTotalPrice(CreateOrderRequest request, Integer orderTotalPrice) {
        // 클라이언트에서 받아온 총 금액과 실제 상품 금액을 다 합한 총 금액이 같은지 검증
        if (!request.totalPrice().equals(orderTotalPrice)) {
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
