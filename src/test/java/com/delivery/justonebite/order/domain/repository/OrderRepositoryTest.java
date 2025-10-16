package com.delivery.justonebite.order.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.delivery.justonebite.order.domain.entity.Order;
import com.delivery.justonebite.order.domain.enums.OrderStatus;
import com.delivery.justonebite.shop.domain.entity.Shop;
import com.delivery.justonebite.shop.domain.repository.ShopRepository;
import com.delivery.justonebite.user.domain.entity.User;
import com.delivery.justonebite.user.domain.entity.UserRole;
import com.delivery.justonebite.user.domain.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderRepositoryTest {

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
    private UUID TEST_ORDER_ID;
    private final Long OWNER_ID = 2L;

    @BeforeEach
    void setUp() {
        // 이전 테스트 잔재 삭제
        // 참조하는 자식 엔티티 먼저 삭제
        orderHistoryRepository.deleteAllInBatch();
        orderItemRepository.deleteAllInBatch();
        // 부모 엔티티 삭제
        orderRepository.deleteAllInBatch();

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
    }

    @Test
    @DisplayName("saveOrder")
    void saveOrder() {
        // testOrder 테스트
        assertThat(testOrder).isNotNull();
        assertThat(testOrder.getId()).isNotNull();
        assertThat(testOrder.getShop()).isNotNull();
        assertThat(testOrder.getCurrentStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(testOrder.getOrderRequest()).contains("단무지 빼주세요");
    }

    @Test
    @DisplayName("updateOrder")
    void updateOrder() {
        // 주문 상태 변경
        testOrder.updateCurrentStatus(OrderStatus.ORDER_ACCEPTED);

        assertThat(testOrder).isNotNull();
        assertThat(testOrder.getId()).isNotNull();
        assertThat(testOrder.getShop().getOwnerId()).isEqualTo(OWNER_ID);
        assertThat(testOrder.getCurrentStatus()).isEqualTo(OrderStatus.ORDER_ACCEPTED);
    }

    @Test
    @DisplayName("existsByIdAndCustomer_Id : 주문 ID와 고객 ID가 일치할 때 True 반환")
    void existsByIdAndCustomer_Id_Returns_True() {
        boolean exists = orderRepository.existsByIdAndCustomer_Id(TEST_ORDER_ID, customer.getId());
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByIdAndShop_OwnerId : 주문 ID와 가게 주인 ID가 일치할 때 True 반환")
    void existsByIdAndShop_OwnerId_Returns_True() {
        boolean exists = orderRepository.existsByIdAndShop_OwnerId(TEST_ORDER_ID, OWNER_ID);
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("findAllByShop_Id : 가게 ID로 주문 목록을 페이징하여 조회")
    void findAllByShop_Id() {
        // 저장값 총 11개 되는 것을 방지하기 위해 전체 삭
        orderRepository.deleteAll();

        for (int i=0; i<10; i++) {
            Order order = Order.create(
                customer,
                shop,
                shop.getAddress(),
                customer.getPhoneNumber(),
                "테스트 주문 " + (i+1),
                55000,
                OrderStatus.PENDING,
                "단무지 빼주세요",
                "문 앞에 놓아주세요"
            );

            orderRepository.save(order);
        }

        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Page<Order> orders = orderRepository.findAllByShop_Id(shopId, pageable);

        assertThat(orders).isNotNull();
        assertFalse(orders.isEmpty());
        assertThat(orders.getTotalElements()).isEqualTo(10);
        assertThat(orders.getContent()).hasSize(10);
        assertThat(orders.getContent().get(0).getOrderName()).contains("테스트 주문 10");
    }

    @Test
    @DisplayName("findByIdWithCustomer : Customer와 JOIN FETCH하여 N+1 없이 주문 상세 조회")
    void findByIdWithCustomer() {
        Order order = Order.create(
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

        Order savedOrder = orderRepository.save(order);
        UUID savedOrderId = savedOrder.getId();

        Optional<Order> result = orderRepository.findByIdWithCustomer(savedOrderId);

        assertTrue(result.isPresent());

        Order found = result.get();

        assertEquals(savedOrderId, found.getId());
        // Fetch Join 확인 (레이지 로딩 exception 없이 Customer에 접근 가능한지)
        assertEquals(customer.getId(), found.getCustomer().getId());
    }
}