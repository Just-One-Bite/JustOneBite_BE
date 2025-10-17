package com.delivery.justonebite.order.domain.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.delivery.justonebite.order.domain.entity.Order;
import com.delivery.justonebite.order.domain.entity.OrderHistory;
import com.delivery.justonebite.order.domain.enums.OrderStatus;
import com.delivery.justonebite.shop.domain.entity.Shop;
import com.delivery.justonebite.shop.domain.repository.ShopRepository;
import com.delivery.justonebite.user.domain.entity.User;
import com.delivery.justonebite.user.domain.entity.UserRole;
import com.delivery.justonebite.user.domain.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderHistoryRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderHistoryRepository orderHistoryRepository;

    private User customer;
    private Shop shop;
    private Long userId;
    private UUID shopId;
    private Order testOrder;
    private Order anotherOrder;
    private UUID TEST_ORDER_ID;
    private UUID ANOTHER_ORDER_ID;
    private UUID ANOTHER_SHOP_ID;
    private final Long OWNER_ID = 2L;

    @BeforeEach
    void setUp() {
        customer = User.builder()
            .name("홍길동")
            .email("gildong@gmail.com")
            .password("Qwerty1234@")
            .phoneNumber("010-2222-2222")
            .userRole(UserRole.CUSTOMER)
            .build();

        User savedCustomer = userRepository.save(customer);
        userId = savedCustomer.getId();

        shop = Shop.builder()
            .ownerId(OWNER_ID)
            .name("Test Shop")
            .registrationNumber("123454523423443")
            .province("서울특별시")
            .city("서울특별시")
            .district("종로구")
            .address("종로구 사직로 133-25")
            .phoneNumber("010-0000-0000")
            .operatingHour("13:00 ~ 21:00")
            .createdAt(LocalDateTime.now())
            .createdBy(OWNER_ID)
            .updatedAt(LocalDateTime.now())
            .updatedBy(OWNER_ID)
            .build();

        Shop savedShop = shopRepository.save(shop);
        shopId = savedShop.getId();

        testOrder = Order.create(
            customer,
            shop,
            shop.getAddress(),
            customer.getPhoneNumber(),
            "마라탕 외 2건",
            55000,
            OrderStatus.PENDING,
            "단무지 빼주세요",
            "문 앞에 놓아주세요"
        );

        Order savedOrder = orderRepository.save(testOrder);
        TEST_ORDER_ID = savedOrder.getId();

        anotherOrder = Order.create(
            customer,
            shop,
            shop.getAddress(),
            customer.getPhoneNumber(),
            "떡볶이 외 2건",
            30000,
            OrderStatus.DELIVERING,
            "단무지 빼주세요",
            "문 앞에 놓아주세요"
        );

        Order savedAnotherOrder = orderRepository.save(anotherOrder);
        ANOTHER_ORDER_ID = savedAnotherOrder.getId();

        // TestOrder : 주문 이력 저장
        orderHistoryRepository.save(OrderHistory.create(testOrder, OrderStatus.PENDING));
        orderHistoryRepository.save(OrderHistory.create(testOrder, OrderStatus.ORDER_ACCEPTED));
        orderHistoryRepository.save(OrderHistory.create(testOrder, OrderStatus.PREPARING));
        orderHistoryRepository.save(OrderHistory.create(testOrder, OrderStatus.DELIVERING));

        // AnotherOrder : 주문 이력 저장
        orderHistoryRepository.save(OrderHistory.create(anotherOrder, OrderStatus.PENDING));
        orderHistoryRepository.save(OrderHistory.create(anotherOrder, OrderStatus.ORDER_CANCELLED));
    }

    @Test
    @DisplayName("findTopByOrder_IdOrderByCreatedAtDesc : 최신 상태 하나를 반환")
    void findTopByOrder_IdOrderByCreatedAtDesc() {
        Optional<OrderHistory> history = orderHistoryRepository.findTopByOrder_IdOrderByCreatedAtDesc(
            TEST_ORDER_ID);

        assertTrue(history.isPresent());
        assertEquals(testOrder, history.get().getOrder());
        assertEquals(testOrder.getId(), history.get().getOrder().getId());
        // 가장 최근 이력의 상태 반환
        assertThat(history.get().getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }

    @Test
    @DisplayName("findAllByOrder_IdOrderByCreatedAtDesc : 전체 목록을 최신순으로 반환")
    void findAllByOrder_IdOrderByCreatedAtDesc() {
        List<OrderHistory> testOrderHistories = orderHistoryRepository.findAllByOrder_IdOrderByCreatedAtDesc(
            TEST_ORDER_ID);

        List<OrderHistory> anotherOrderHistories = orderHistoryRepository.findAllByOrder_IdOrderByCreatedAtDesc(
            ANOTHER_ORDER_ID);

        assertFalse(testOrderHistories.isEmpty());
        assertThat(testOrderHistories.size()).isEqualTo(4);
        assertThat(testOrderHistories.getFirst().getStatus()).isEqualTo(OrderStatus.DELIVERING);
        assertThat(testOrderHistories.getLast().getStatus()).isEqualTo(OrderStatus.PENDING);

        assertFalse(anotherOrderHistories.isEmpty());
        assertThat(anotherOrderHistories.size()).isEqualTo(2);
        assertThat(anotherOrderHistories.getFirst().getStatus()).isEqualTo(OrderStatus.ORDER_CANCELLED);
        assertThat(anotherOrderHistories.getLast().getStatus()).isEqualTo(OrderStatus.PENDING);
    }

    @Test
    @DisplayName("existsByOrder_Shop_IdAndStatusNot : 가게에서 완료되지 않은 배달 주문이 있는지 확인")
    void existsByOrder_Shop_IdAndStatusNot() {
        boolean exists = orderHistoryRepository.existsByOrder_Shop_IdAndStatusNot(shopId, OrderStatus.COMPLETED);
        assertTrue(exists);
    }
}