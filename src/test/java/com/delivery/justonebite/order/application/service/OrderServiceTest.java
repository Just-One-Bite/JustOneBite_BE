package com.delivery.justonebite.order.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

import com.delivery.justonebite.global.exception.custom.CustomException;
import com.delivery.justonebite.global.exception.response.ErrorCode;
import com.delivery.justonebite.item.domain.entity.Item;
import com.delivery.justonebite.item.domain.repository.ItemRepository;
import com.delivery.justonebite.order.application.stub.OrderStubData;
import com.delivery.justonebite.order.application.stub.OrderTestMocks;
import com.delivery.justonebite.order.domain.entity.Order;
import com.delivery.justonebite.order.domain.entity.OrderHistory;
import com.delivery.justonebite.order.domain.entity.OrderItem;
import com.delivery.justonebite.order.domain.enums.OrderStatus;
import com.delivery.justonebite.order.domain.factory.OrderFactory;
import com.delivery.justonebite.order.domain.repository.OrderHistoryRepository;
import com.delivery.justonebite.order.domain.repository.OrderItemRepository;
import com.delivery.justonebite.order.domain.repository.OrderRepository;
import com.delivery.justonebite.order.presentation.dto.request.CancelOrderRequest;
import com.delivery.justonebite.order.presentation.dto.request.CreateOrderRequest;
import com.delivery.justonebite.order.presentation.dto.request.UpdateOrderStatusRequest;
import com.delivery.justonebite.order.presentation.dto.response.CustomerOrderResponse;
import com.delivery.justonebite.order.presentation.dto.response.GetOrderStatusResponse;
import com.delivery.justonebite.order.presentation.dto.response.OrderCancelResponse;
import com.delivery.justonebite.order.presentation.dto.response.OrderDetailsResponse;
import com.delivery.justonebite.shop.domain.entity.Shop;
import com.delivery.justonebite.shop.domain.repository.ShopRepository;
import com.delivery.justonebite.user.domain.entity.User;
import com.delivery.justonebite.user.domain.entity.UserRole;
import com.delivery.justonebite.user.domain.repository.AddressRepository;
import com.delivery.justonebite.user.domain.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    private final Long USER_ID = 1L;

    @Mock
    private UserRepository userRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ShopRepository shopRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private OrderHistoryRepository orderHistoryRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private OrderFactory orderFactory;

    @InjectMocks
    private OrderService orderService;

    private User mockCustomer;
    private User mockOwner;

    @BeforeEach
    void setUp() {
        mockCustomer = mock(User.class);
        lenient().doReturn(USER_ID).when(mockCustomer).getId();
        lenient().doReturn(UserRole.CUSTOMER).when(mockCustomer).getUserRole();

        mockOwner = mock(User.class);
        lenient().doReturn(2L).when(mockOwner).getId();
        lenient().doReturn(UserRole.OWNER).when(mockOwner).getUserRole();

        // Stubbing 안전하게 하기 위해 given 대신 lenient 사용
        // UserService 내 authorizeUser/authorizeCustomer 통과를 위한 Stubbing
        lenient().doReturn(Optional.of(mockCustomer)).when(userRepository).findById(eq(USER_ID));
        lenient().doReturn(Optional.of(mockOwner)).when(userRepository).findById(eq(2L));
        lenient().doReturn(Optional.of(mockCustomer)).when(userRepository).findById(anyLong());

        SecurityContextHolder.getContext().setAuthentication(auth(USER_ID, UserRole.CUSTOMER));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private UsernamePasswordAuthenticationToken auth(Long userId, UserRole userRole) {
        String role = userRole.getRole();
        if (!role.startsWith("ROLE_"))
            role = "ROLE_" + role;

        return new UsernamePasswordAuthenticationToken(
            userId, "N/A", List.of(new SimpleGrantedAuthority(role))
        );
    }


    @Test
    @DisplayName("createOrder: 주문 생성 성공")
    void createOrder() {
        UUID orderId = UUID.randomUUID();
        UUID shopId = UUID.randomUUID();

        final int expectedTotalPrice = 35000;
        Order mockOrder = OrderTestMocks.mockOrder(orderId, mockCustomer, expectedTotalPrice);

        List<Item> expectedFoundItems = List.of(OrderTestMocks.mockItem(UUID.randomUUID(), 15000), OrderTestMocks.mockItem(UUID.randomUUID(), 20000));

        Shop mockShop = OrderTestMocks.mockShop(shopId);

        CreateOrderRequest request = new CreateOrderRequest(
            shopId,
            UUID.randomUUID(),
            "010-1234-5678",
            "단무지 빼주세요.",
            "문 앞에 놓아주세요",
            OrderStubData.MockData.getMockOrderItemsDto(),
            expectedTotalPrice
        );

        given(itemRepository.findAllByItemIdIn(any(List.class)))
            .willReturn(expectedFoundItems);
        given(shopRepository.findById(shopId)).willReturn(Optional.of(mockShop));

        // OrderFactory 반환 설정
        given(
            orderFactory.create(any(User.class), any(Shop.class), anyString(), any(CreateOrderRequest.class),
                anyMap()))
            .willReturn(mockOrder);
        given(orderFactory.getOrderItems(any(Order.class), anyList(), anyMap()))
            .willReturn(Collections.emptyList()); // OrderItem 저장 생략

        // when
        orderService.createOrder(request, mockCustomer);

        // then
        then(orderRepository).should().save(mockOrder);
        then(orderItemRepository).should(times(1)).saveAll(anyList());
        then(orderHistoryRepository).should().save(any(OrderHistory.class));
        then(itemRepository).should(times(1)).findAllByItemIdIn(any(List.class));
    }

    @Test
    @DisplayName("createOrder : 주문 생성 에러 (유저 권한이 CUSTOMER가 아닐 경우 - FORBIDDEN_ACCESS)")
    void createOrderFromOwner() {
        UUID shopId = UUID.randomUUID();

        final int expectedTotalPrice = 35000;

        CreateOrderRequest request = new CreateOrderRequest(
            shopId,
            UUID.randomUUID(),
            "010-1234-5678",
            "단무지 빼주세요.",
            "문 앞에 놓아주세요",
            OrderStubData.MockData.getMockOrderItemsDto(),
            expectedTotalPrice
        );

        // 예외 코드 검증
        assertThatThrownBy(() -> orderService.createOrder(request, mockOwner))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FORBIDDEN_ACCESS);

        // 저장 로직 실행 여부 검증
        then(orderRepository).should(times(0)).save(any(Order.class));
        then(orderHistoryRepository).should(times(0)).save(any(OrderHistory.class));

        // 권한 검증 이후, 리포지토리/팩토리 호출 없어야 함
        then(itemRepository).should(times(0)).findAllByItemIdIn(anyList());
        then(orderFactory).should(times(0)).create(any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("createOrder : 주문 생성 에러 (클라이언트로부터 받아온 총 금액값이 서버에서 계산된 총 금액값과 불일치 - BAD_REQUEST)")
    void createOrderTotalPriceNotMatch() {
        UUID shopId = UUID.randomUUID();
        final int clientRequestTotalPrice = 35000;
        final int calculatedTotalPrice = 40000;

        List<Item> expectedFoundItems = List.of(OrderTestMocks.mockItem(UUID.randomUUID(), 15000),
            OrderTestMocks.mockItem(UUID.randomUUID(), 20000));

        CreateOrderRequest request = new CreateOrderRequest(
            shopId,
            UUID.randomUUID(),
            "010-1234-5678",
            "단무지 빼주세요.",
            "문 앞에 놓아주세요",
            OrderStubData.MockData.getMockOrderItemsDto(),
            clientRequestTotalPrice
        );

        // OrderFactory가 반환하는 객체 : getTotalPrice() 호출 시 서버 계산 금액 반환해야 함!
        Order mockOrder = OrderTestMocks.mockOrder(UUID.randomUUID(), mockCustomer, calculatedTotalPrice);
        Shop mockShop = OrderTestMocks.mockShop(shopId);

        given(itemRepository.findAllByItemIdIn(any(List.class)))
            .willReturn(expectedFoundItems);
        given(shopRepository.findById(shopId)).willReturn(Optional.of(mockShop));

        // OrderFactory에서 금액 불일치가 발생할 Order 객체를 반환하도록 설정
        given(
            orderFactory.create(any(User.class), any(Shop.class), anyString(), any(CreateOrderRequest.class),
                anyMap()))
            .willReturn(mockOrder);

        // then
        // 예외 코드 검증
        assertThatThrownBy(() -> orderService.createOrder(request, mockCustomer))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.TOTAL_PRICE_NOT_MATCH);

        // 저장 로직 실행 여부 검증 (예외로 인해 실행되지 않아야 함)
        then(orderRepository).should(times(0)).save(any(Order.class));
        then(orderHistoryRepository).should(times(0)).save(any(OrderHistory.class));

        // 호출 여부 검증
        then(orderFactory).should(times(1)).create(any(), any(), any(), any(), any());
        then(itemRepository).should(times(1)).findAllByItemIdIn(anyList());
    }

    /**
     * 주문 목록 조회
     */
    @Test
    @DisplayName("getCustomerOrders : 고객 주문 목록 조회 성공 (페이지)")
    void getCustomerOrders() {
        UUID shopId = UUID.randomUUID();
        final int page = 0; // 0-based
        final int size = 10;
        final String sortBy = "createdAt";

        Order mockOrder = OrderTestMocks.mockOrder(UUID.randomUUID(), mockCustomer, 25000);
        List<Order> orderList = List.of(mockOrder);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortBy));

        // 레포지토리 스터빙
        given(orderRepository.findAll(any(Pageable.class)))
            .willReturn(new PageImpl<>(orderList, pageable, 1));

        Page<CustomerOrderResponse> result =
            orderService.getCustomerOrders(page, size, sortBy, mockCustomer);

        // 반환된 페이지 내용 검증
        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());

        // 호출 횟수 검증
        then(orderRepository).should(times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("getCustomerOrders : 고객 주문 목록 조회 실패 (유저 권한이 CUSTOMER가 아닐 경우 - FORBIDDEN_ACCESS)")
    void getCustomerOrdersFromOwner() {
        final int page = 0;
        final int size = 10;
        final String sortBy = "createdAt";

        // 예외 코드 검증
        assertThatThrownBy(() -> orderService.getCustomerOrders(page, size, sortBy, mockOwner))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FORBIDDEN_ACCESS);

        // 호출 검증
        then(orderRepository).should(times(0)).findAll(any(Pageable.class));
    }

    /**
     * 주문 상세정보 조회
     */
    @Test
    @DisplayName("getOrderDetails : 주문 상세정보 조회 성공")
    void getOrderDetails() {
        UUID orderId = UUID.randomUUID();
        UUID shopId = UUID.randomUUID();

        final int totalPrice = 50000;
        Order mockOrder = OrderTestMocks.mockOrder(orderId, mockCustomer, totalPrice);

        List<OrderItem> mockOrderItems = List.of(
            OrderTestMocks.mockOrderItem(UUID.randomUUID(), 10000),
            OrderTestMocks.mockOrderItem(UUID.randomUUID(), 20000)
        );

        // 레포지토리 스터빙 (주문 조회 & 주문 아이템 목록 조회)
        given(orderRepository.findById(orderId)).willReturn(Optional.of(mockOrder));
        given(orderItemRepository.findAllByOrder(mockOrder)).willReturn(mockOrderItems);

        OrderDetailsResponse response = orderService.getOrderDetails(orderId, mockCustomer);

        // 반환 검증
        assertThat(response).isNotNull();
        assertEquals(orderId, response.orderId(), "주문 ID가 일치해야 합니다.");

        // 호출 검증
        then(orderRepository).should(times(1)).findById(orderId);
        then(orderItemRepository).should(times(1)).findAllByOrder(mockOrder);
    }

    @Test
    @DisplayName("getOrderDetails : 주문을 찾을 수 없는 경우 - NOT_FOUND")
    void getOrderDetailsOrderNotFound() {
        UUID orderId = UUID.randomUUID();

        // findById 호출 시 빈 값 반환하도록 설정
        given(orderRepository.findById(eq(orderId))).willReturn(Optional.empty());

        // 예외 코드 검증
        assertThatThrownBy(() -> orderService.getOrderDetails(orderId, mockCustomer))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RESOURCE_NOT_FOUND);

        // 호출 검증
        then(orderRepository).should(times(1)).findById(eq(orderId));
        then(orderItemRepository).should(times(0)).findAllByOrder(any(Order.class));
    }

    /**
     * 주문 상태 변경
     */
    @Test
    @DisplayName("updateOrderStatus : 주문 상태 변경 성공")
    void updateOrderStatus() {
        UUID orderId = UUID.randomUUID();
        final OrderStatus NEW_STATUS = OrderStatus.DELIVERING;

        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest(
            NEW_STATUS.name()
        );

        final int totalPrice = 50000;
        Order mockOrder = OrderTestMocks.mockOrder(orderId, mockOwner, totalPrice);

        // 주문 조회
        given(orderRepository.findById(orderId)).willReturn(Optional.of(mockOrder));

        orderService.updateOrderStatus(orderId, request, mockOwner);

        // 호출 검증
        then(mockOrder).should(times(1)).updateCurrentStatus(NEW_STATUS);
        then(orderRepository).should(times(1)).findById(orderId);

        // 저장 검증
        then(orderHistoryRepository).should(times(1)).save(any(OrderHistory.class));
    }

    @Test
    @DisplayName("updateOrderStatus : 주문 상태 변경 실패 (OWNER 권한이 아닐 경우 - FORBIDDEN_ACCESS)")
    void updateOrderStatusFromCustomer() {
        UUID orderId = UUID.randomUUID();
        UUID shopId = UUID.randomUUID();
        final OrderStatus NEW_STATUS = OrderStatus.DELIVERING;

        final int totalPrice = 50000;
        Order mockOrder = OrderTestMocks.mockOrder(orderId, mockCustomer, totalPrice);

        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest(
            NEW_STATUS.name()
        );

        // 예외 코드 검증
        assertThatThrownBy(() -> orderService.updateOrderStatus(orderId, request, mockCustomer))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FORBIDDEN_ACCESS);

        // 호출 검증
        then(mockOrder).should(times(0)).updateCurrentStatus(any(OrderStatus.class));
        then(orderRepository).should(times(0)).findById(any(UUID.class));
        then(orderHistoryRepository).should(times(0)).save(any());
    }


    /**
     * 주문 상태 이력 조회
     */
    @Test
    @DisplayName("getOrderStatusHistories : 주문 상태 이력 조회 (성공)")
    void getOrderStatusHistories() {
        UUID orderId = UUID.randomUUID();
        UUID shopId = UUID.randomUUID();
        final int totalPrice = 50000;

        Order mockOrder = OrderTestMocks.mockOrder(orderId, mockCustomer, totalPrice);

        List<OrderHistory> mockOrderHistories = List.of(
            OrderTestMocks.mockOrderHistory(mockOrder, OrderStatus.PENDING),
            OrderTestMocks.mockOrderHistory(mockOrder, OrderStatus.DELIVERING),
            OrderTestMocks.mockOrderHistory(mockOrder, OrderStatus.COMPLETED)
        );

        given(orderHistoryRepository.findAllByOrder_IdOrderByCreatedAtDesc(orderId)).willReturn(
            mockOrderHistories);
        given(orderRepository.existsByIdAndCustomer_Id(eq(orderId), eq(USER_ID)))
            .willReturn(true);

        GetOrderStatusResponse response = orderService.getOrderStatusHistories(orderId, mockCustomer);

        // then
        assertThat(response).isNotNull();
        assertThat(response.history().getFirst().status()).isEqualTo(OrderStatus.PENDING.name());

        // 호출 검증
        then(orderRepository).should(times(1)).existsByIdAndCustomer_Id(eq(orderId), eq(USER_ID));
        then(orderHistoryRepository).should(times(1)).findAllByOrder_IdOrderByCreatedAtDesc(orderId);
    }

    @Test
    @DisplayName("getOrderStatusHistories : 주문을 찾을 수 없는 경우 - NOT_FOUND")
    void getOrderStatusHistoriesOrderNotFound() {
        UUID orderId = UUID.randomUUID();

        // 권한 확인
        given(orderRepository.existsByIdAndCustomer_Id(eq(orderId), eq(USER_ID)))
            .willReturn(true);
        // 비어있는 리스트를 반환 설정
        given(orderHistoryRepository.findAllByOrder_IdOrderByCreatedAtDesc(orderId))
            .willReturn(Collections.emptyList());

        // 예외 코드 검증
        assertThatThrownBy(() -> orderService.getOrderStatusHistories(orderId, mockCustomer))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ORDER_STATUS_NOT_FOUND);

        // 호출 검증
        then(orderRepository).should(times(1)).existsByIdAndCustomer_Id(eq(orderId), eq(USER_ID));
        then(orderHistoryRepository).should(times(1)).findAllByOrder_IdOrderByCreatedAtDesc(orderId);
    }

    /**
     * 주문 취소
     */
    @Test
    @DisplayName("cancelOrder : 주문 취소 (성공)")
    void cancelOrder() {
        UUID orderId = UUID.randomUUID();
        UUID shopId = UUID.randomUUID();
        final int totalPrice = 50000;

        CancelOrderRequest request = new CancelOrderRequest(OrderStatus.ORDER_CANCELLED.name());

        Order mockOrder = OrderTestMocks.mockOrder(orderId, mockCustomer, totalPrice);

        given(orderRepository.findByIdWithCustomer(eq(orderId)))
            .willReturn(Optional.of(mockOrder));
        given(mockOrder.getCurrentStatus()).willReturn(OrderStatus.ORDER_CANCELLED);

        OrderCancelResponse response = orderService.cancelOrder(request, orderId, mockCustomer);

        // 반환 검증
        assertThat(response).isNotNull();
        assertThat(response.orderId()).isEqualTo(orderId);
        assertEquals(response.orderStatus(), OrderStatus.ORDER_CANCELLED.name());

        // 호출 검증
        then(mockOrder).should(times(1)).updateCurrentStatus(OrderStatus.ORDER_CANCELLED);
        then(orderHistoryRepository).should(times(1)).save(any(OrderHistory.class));
        then(orderRepository).should(times(1)).findByIdWithCustomer(orderId);
        then(orderHistoryRepository).should(times(1)).save(any(OrderHistory.class));
    }

    @Test
    @DisplayName("cancelOrder : 주문 취소 실패 (현재 주문 상태가 PENDING이 아닐 경우 - BAD REQUEST)")
    void cancelOrderOrderStatusIsNotPending() {
        UUID orderId = UUID.randomUUID();
        UUID shopId = UUID.randomUUID();
        final int totalPrice = 50000;

        CancelOrderRequest request = new CancelOrderRequest(OrderStatus.ORDER_CANCELLED.name());

        Order mockOrder = OrderTestMocks.mockOrder(orderId, mockCustomer, totalPrice);

        given(orderRepository.findByIdWithCustomer(eq(orderId)))
            .willReturn(Optional.of(mockOrder));

        // Order 엔티티의 상태 변경 메서드가 예외를 던지도록 설정
        doThrow(new CustomException(ErrorCode.ORDER_STATUS_CANCEL_NOT_ALLOWED))
            .when(mockOrder).updateCurrentStatus(OrderStatus.ORDER_CANCELLED);

        // 예외 코드 검증
        assertThatThrownBy(() -> orderService.cancelOrder(request, orderId, mockCustomer))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ORDER_STATUS_CANCEL_NOT_ALLOWED);

        // 호출 검증
        then(orderHistoryRepository).should(times(0)).save(any(OrderHistory.class));
        then(mockOrder).should(times(1)).updateCurrentStatus(OrderStatus.ORDER_CANCELLED);
    }

    @Test
    @DisplayName("cancelOrder : 주문 취소 실패 (주문 시점으로부터 5분이 초과하였을 경우 - BAD REQUEST)")
    void cancelOrderTimeExceed() {
        UUID orderId = UUID.randomUUID();
        UUID shopId = UUID.randomUUID();
        final int totalPrice = 50000;

        CancelOrderRequest request = new CancelOrderRequest(OrderStatus.ORDER_CANCELLED.name());

        Order mockOrder = OrderTestMocks.mockOrder(orderId, mockCustomer, totalPrice);

        // Order 엔티티가 5분 이상 지난 시각을 반환하도록 설정
        // Order.updateCurrentStatus 내부 시간 검증 로직에 사용됨
        LocalDateTime pastTime = LocalDateTime.now().minusMinutes(10);
        lenient().doReturn(pastTime).when(mockOrder).getCreatedAt();

        given(orderRepository.findByIdWithCustomer(eq(orderId)))
            .willReturn(Optional.of(mockOrder));

        doThrow(new CustomException(ErrorCode.ORDER_CANCEL_TIME_EXCEEDED))
            .when(mockOrder).updateCurrentStatus(OrderStatus.ORDER_CANCELLED);

        // 예외 코드 검증
        assertThatThrownBy(() -> orderService.cancelOrder(request, orderId, mockCustomer))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ORDER_CANCEL_TIME_EXCEEDED);

        // 호출 검증
        then(orderHistoryRepository).should(times(0)).save(any(OrderHistory.class));
        then(mockOrder).should(times(1)).updateCurrentStatus(OrderStatus.ORDER_CANCELLED);
    }
}