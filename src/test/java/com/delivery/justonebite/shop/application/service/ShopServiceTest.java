package com.delivery.justonebite.shop.application.service;

import com.delivery.justonebite.global.common.entity.BaseEntity;
import com.delivery.justonebite.global.exception.custom.CustomException;
import com.delivery.justonebite.global.exception.response.ErrorCode;
import com.delivery.justonebite.order.domain.enums.OrderStatus;
import com.delivery.justonebite.order.domain.repository.OrderHistoryRepository;
import com.delivery.justonebite.order.domain.repository.OrderItemRepository;
import com.delivery.justonebite.order.domain.repository.OrderRepository;
import com.delivery.justonebite.review.repository.ReviewRepository;
import com.delivery.justonebite.shop.domain.entity.Category;
import com.delivery.justonebite.shop.domain.entity.Shop;
import com.delivery.justonebite.shop.domain.repository.CategoryRepository;
import com.delivery.justonebite.shop.domain.repository.ShopRepository;
import com.delivery.justonebite.shop.presentation.dto.request.ShopCreateRequest;
import com.delivery.justonebite.shop.presentation.dto.request.ShopUpdateRequest;
import com.delivery.justonebite.shop.presentation.dto.response.ShopDeleteResponse;
import com.delivery.justonebite.shop.presentation.dto.response.ShopUpdateResponse;
import com.delivery.justonebite.user.domain.entity.User;
import com.delivery.justonebite.user.domain.entity.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ShopServiceTest {

    private ShopRepository shopRepository;
    private CategoryRepository categoryRepository;
    private OrderRepository orderRepository;
    private OrderHistoryRepository orderHistoryRepository;
    private OrderItemRepository orderItemRepository;
    private ReviewRepository reviewRepository;

    private ShopService shopService;

    private User customer;
    private User owner;
    private Shop shop;

    @BeforeEach
    void setUp() {
        shopRepository = mock(ShopRepository.class);
        categoryRepository = mock(CategoryRepository.class);
        orderRepository = mock(OrderRepository.class);
        orderHistoryRepository = mock(OrderHistoryRepository.class);
        orderItemRepository = mock(OrderItemRepository.class);
        reviewRepository = mock(ReviewRepository.class);

        shopService = new ShopService(shopRepository, categoryRepository, orderRepository, orderHistoryRepository, orderItemRepository);

        // 기본 유저 & 샵 세팅
        customer = User.builder()
                .id(1L)
                .email("customer@gmail.com")
                .name("고객")
                .userRole(UserRole.CUSTOMER)
                .build();

        owner = User.builder()
                .id(2L)
                .email("owner@gmail.com")
                .name("사장님")
                .userRole(UserRole.OWNER)
                .build();

        shop = Shop.builder()
                .id(UUID.randomUUID())
                .ownerId(owner.getId())
                .name("테스트 치킨집")
                .registrationNumber("123-45-6789")
                .province("서울")
                .city("강남구")
                .district("역삼동")
                .address("서울 강남구 테헤란로 123")
                .phoneNumber("02-0000-0000")
                .description("테스트용")
                .operatingHour("10:00 - 22:00")
                .build();
    }

    @Test
    @DisplayName("가게 등록 테스트")
    void createShop_success() {
        List<String> categories = List.of("치킨", "피자");
        ShopCreateRequest req = ShopCreateRequest.builder()
                .name("새로운 가게")
                .registrationNumber("999-88-7777")
                .province("서울")
                .city("강남구")
                .district("역삼동")
                .address("테헤란로 123")
                .phoneNumber("02-9876-5432")
                .description("신규 오픈!")
                .operatingHour("09:00 - 21:00")
                .categories(categories)
                .build();

        Category chicken = Category.builder().categoryName("치킨").build();
        Category pizza = Category.builder().categoryName("피자").build();
        given(categoryRepository.findAllByCategoryNameIn(categories)).willReturn(List.of(chicken, pizza));
        given(shopRepository.save(any(Shop.class))).willReturn(shop);

        Shop created = shopService.createShop(req, customer.getId(), UserRole.CUSTOMER);

        assertThat(created).isNotNull();
        assertThat(created.getName()).isEqualTo("테스트 치킨집");
        verify(shopRepository).save(any(Shop.class));
    }

    @Test
    @DisplayName("OWNER는 가게 등록이 불가능")
    void createShop_invalidRole() {
        ShopCreateRequest req = ShopCreateRequest.builder()
                .name("가짜 가게")
                .registrationNumber("111-22-3333")
                .province("서울")
                .city("강남구")
                .district("논현동")
                .address("강남대로 321")
                .phoneNumber("02-5555-6666")
                .description("테스트용")
                .operatingHour("10:00 - 23:00")
                .categories(List.of("분식"))
                .build();

        assertThatThrownBy(() -> shopService.createShop(req, owner.getId(), UserRole.OWNER))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_USER_ROLE);
    }


    @Test
    @DisplayName("가게 수정 테스트")
    void updateShop_success() {
        ShopUpdateRequest req = ShopUpdateRequest.builder()
                .name("수정된 가게")
                .phone_number("02-7777-8888")
                .operating_hour("11:00 - 21:00")
                .description("업데이트 완료")
                .categories(List.of("피자"))
                .build();

        shop.getClass().getSuperclass(); // BaseEntity 접근
        try {
            Field updatedByField = BaseEntity.class.getDeclaredField("updatedBy");
            updatedByField.setAccessible(true);
            updatedByField.set(shop, owner.getId());

            Field updatedAtField = BaseEntity.class.getDeclaredField("updatedAt");
            updatedAtField.setAccessible(true);
            updatedAtField.set(shop, LocalDateTime.now());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        given(shopRepository.findById(shop.getId())).willReturn(Optional.of(shop));
        Category pizza = Category.builder().categoryName("피자").build();
        given(categoryRepository.findByCategoryName("피자")).willReturn(Optional.of(pizza));

        ShopUpdateResponse updated = shopService.updateShop(req, shop.getId(), owner.getId(), UserRole.OWNER);

        assertThat(updated).isNotNull();
        assertThat(updated.updatedBy()).isEqualTo(owner.getId());
        verify(shopRepository, never()).save(any());
    }

    @Test
    @DisplayName("완료되지 않은 주문이 존재하면 가게 삭제 시 예외 발생")
    void deleteShop_notCompletedOrderExists() {
        given(shopRepository.findByIdAndDeletedAtIsNull(shop.getId())).willReturn(Optional.of(shop));
        given(orderHistoryRepository.existsByOrder_Shop_IdAndStatusNot(shop.getId(), OrderStatus.COMPLETED))
                .willReturn(true);

        assertThatThrownBy(() -> shopService.deleteShop(shop.getId(), owner.getId(), UserRole.OWNER))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_COMPLETED_ORDER_EXISTS);
    }

    @Test
    @DisplayName("CUSTOMER가 가게 삭제 시 예외 발생")
    void deleteShop_invalidRole() {
        given(shopRepository.findByIdAndDeletedAtIsNull(shop.getId())).willReturn(Optional.of(shop));

        assertThatThrownBy(() -> shopService.deleteShop(shop.getId(), customer.getId(), UserRole.CUSTOMER))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_USER_ROLE);
    }

    @Test
    @DisplayName("OWNER가 본인 가게 삭제 시 Soft Delete")
    void deleteShop_success() {
        given(shopRepository.findByIdAndDeletedAtIsNull(shop.getId())).willReturn(Optional.of(shop));
        given(orderHistoryRepository.existsByOrder_Shop_IdAndStatusNot(shop.getId(), OrderStatus.COMPLETED))
                .willReturn(false);

        ShopDeleteResponse response = shopService.deleteShop(shop.getId(), owner.getId(), UserRole.OWNER);

        assertThat(response).isNotNull();
        assertThat(response.deleteAcceptStatus()).isNotNull();
        verify(shopRepository, never()).save(any());
    }
}
