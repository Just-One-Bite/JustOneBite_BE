package com.delivery.justonebite.review.application.service;

import com.delivery.justonebite.global.exception.custom.CustomException;
import com.delivery.justonebite.global.exception.response.ErrorCode;
import com.delivery.justonebite.order.domain.entity.Order;
import com.delivery.justonebite.order.domain.entity.OrderHistory;
import com.delivery.justonebite.order.domain.enums.OrderStatus;
import com.delivery.justonebite.order.domain.repository.OrderHistoryRepository;
import com.delivery.justonebite.order.domain.repository.OrderRepository;
import com.delivery.justonebite.review.entity.Review;
import com.delivery.justonebite.review.presentation.dto.request.CreateReviewRequest;
import com.delivery.justonebite.review.presentation.dto.request.UpdateReviewRequest;
import com.delivery.justonebite.review.presentation.dto.response.CreateReviewResponse;
import com.delivery.justonebite.review.presentation.dto.response.ReviewResponse;
import com.delivery.justonebite.review.repository.ReviewRepository;
import com.delivery.justonebite.shop.domain.entity.Shop;
import com.delivery.justonebite.user.domain.entity.User;
import com.delivery.justonebite.user.domain.entity.UserRole;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    private final Long USER_ID = 1L;

    private ReviewRepository reviewRepository;
    private OrderRepository orderRepository;
    private OrderHistoryRepository orderHistoryRepository;
    private ReviewService sut;

    @BeforeEach
    void setUp() {
        reviewRepository = mock(ReviewRepository.class);
        orderRepository = mock(OrderRepository.class);
        orderHistoryRepository = mock(OrderHistoryRepository.class);
        sut = new ReviewService(reviewRepository, orderRepository, orderHistoryRepository);

        SecurityContextHolder.getContext().setAuthentication(auth(USER_ID, UserRole.CUSTOMER));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private UsernamePasswordAuthenticationToken auth(Long userId, UserRole userRole) {
        String role = userRole.getRole();
        if (!role.startsWith("ROLE_")) role = "ROLE_" + role;

        return new UsernamePasswordAuthenticationToken(
                userId, "N/A", List.of(new SimpleGrantedAuthority(role))
        );
    }

    private Order mockOrder(UUID orderId, Long customerId, UUID shopId) {
        Shop shop = mock(Shop.class);
        given(shop.getId()).willReturn(shopId);

        User customer = mock(User.class);
        given(customer.getId()).willReturn(customerId);

        Order order = mock(Order.class);
        given(order.getId()).willReturn(orderId);
        given(order.getShop()).willReturn(shop);
        given(order.getCustomer()).willReturn(customer);
        return order;
    }

    private OrderHistory makeOrderHistory(OrderStatus status) {
        return OrderHistory.create(null, status);
    }

    // =========== createReview ===========
    @Test
    @DisplayName("createReview: 주문 완료 + 중복 없음이면 성공")
    void create_success() {
        Long currentUserId = 100L;
        UUID orderId = UUID.randomUUID();
        UUID shopId = UUID.randomUUID();

        Order order = mockOrder(orderId, currentUserId, shopId);
        given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

        given(orderHistoryRepository.findTopByOrder_IdOrderByCreatedAtDesc(orderId))
                .willReturn(Optional.of(makeOrderHistory(OrderStatus.COMPLETED)));

        given(reviewRepository.existsByOrder_Id(orderId)).willReturn(false);

        Review reviewCreated = mock(Review.class);
        given(reviewCreated.getReviewId()).willReturn(UUID.randomUUID());
        given(reviewCreated.getOrder()).willReturn(order);
        given(reviewCreated.getShopId()).willReturn(shopId);
        given(reviewCreated.getContent()).willReturn("맛있어요");
        given(reviewCreated.getRating()).willReturn(5);

        given(reviewCreated.getCreatedAt()).willReturn(LocalDateTime.now());
        given(reviewCreated.getCreatedBy()).willReturn(currentUserId);

        try (MockedStatic<Review> reviewStatic = Mockito.mockStatic(Review.class)) {
            reviewStatic.when(() ->
                    Review.create(eq(order), eq(currentUserId), eq(shopId), anyString(), anyInt())
            ).thenReturn(reviewCreated);

            given(reviewRepository.save(reviewCreated)).willReturn(reviewCreated);

            CreateReviewRequest req = new CreateReviewRequest(orderId, shopId, "맛있어요", 5);

            CreateReviewResponse res = sut.createReview(currentUserId, UserRole.CUSTOMER, req);

            assertThat(res).isNotNull();
            then(reviewRepository).should().save(reviewCreated);
        }
    }

    @Test
    @DisplayName("createReview: 권한 없음(OWNER 등) → FORBIDDEN_ACCESS")
    void create_forbidden_role() {
        CreateReviewRequest req = new CreateReviewRequest(UUID.randomUUID(), UUID.randomUUID(), "ok", 4);

        assertThatThrownBy(() -> sut.createReview(1L, UserRole.OWNER, req))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FORBIDDEN_ACCESS);
    }

    @Test
    @DisplayName("createReview: 주문 없음 → ORDER_NOT_FOUND")
    void create_order_not_found() {
        UUID orderId = UUID.randomUUID();
        given(orderRepository.findById(orderId)).willReturn(Optional.empty());

        CreateReviewRequest req = new CreateReviewRequest(orderId, UUID.randomUUID(), "ok", 4);

        assertThatThrownBy(() -> sut.createReview(1L, UserRole.CUSTOMER, req))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ORDER_NOT_FOUND);
    }

    @Test
    @DisplayName("createReview: 주문자 불일치 → FORBIDDEN_ACCESS")
    void create_not_order_owner() {
        Long me = 1L;
        Long other = 2L;
        UUID orderId = UUID.randomUUID();
        UUID shopId = UUID.randomUUID();

        User customer = mock(User.class);
        given(customer.getId()).willReturn(other);

        Order order = mock(Order.class);
        given(order.getCustomer()).willReturn(customer);

        given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

        CreateReviewRequest req = new CreateReviewRequest(orderId, shopId, "ok", 4);

        assertThatThrownBy(() -> sut.createReview(me, UserRole.CUSTOMER, req))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FORBIDDEN_ACCESS);
    }

    @Test
    @DisplayName("createReview: 주문 미완료 → ORDER_NOT_COMPLETED")
    void create_not_completed() {
        Long me = 1L;
        UUID orderId = UUID.randomUUID();
        UUID shopId = UUID.randomUUID();

        User customer = mock(User.class);
        given(customer.getId()).willReturn(me);

        Order order = mock(Order.class);
        given(order.getId()).willReturn(orderId);
        given(order.getCustomer()).willReturn(customer);

        given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
        given(orderHistoryRepository.findTopByOrder_IdOrderByCreatedAtDesc(orderId))
                .willReturn(Optional.of(makeOrderHistory(OrderStatus.PREPARING)));

        CreateReviewRequest req = new CreateReviewRequest(orderId, shopId, "ok", 4);

        assertThatThrownBy(() -> sut.createReview(me, UserRole.CUSTOMER, req))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ORDER_NOT_COMPLETED);
    }

    @Test
    @DisplayName("createReview: 중복 리뷰 → REVIEW_ALREADY_EXISTS")
    void create_duplicate() {
        Long me = 1L;
        UUID orderId = UUID.randomUUID();
        UUID shopId = UUID.randomUUID();

        User customer = mock(User.class);
        given(customer.getId()).willReturn(me);

        Order order = mock(Order.class);
        given(order.getId()).willReturn(orderId);
        given(order.getCustomer()).willReturn(customer);

        given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
        given(orderHistoryRepository.findTopByOrder_IdOrderByCreatedAtDesc(orderId))
                .willReturn(Optional.of(makeOrderHistory(OrderStatus.COMPLETED)));
        given(reviewRepository.existsByOrder_Id(orderId)).willReturn(true);

        CreateReviewRequest req = new CreateReviewRequest(orderId, shopId, "ok", 5);

        assertThatThrownBy(() -> sut.createReview(me, UserRole.CUSTOMER, req))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.REVIEW_ALREADY_EXISTS);
    }

    // ====== 조회 ========
    @Test
    @DisplayName("getById: 존재하면 매핑 성공")
    void getById_ok() {
        UUID rid = UUID.randomUUID();
        Long author = 1L;
        UUID orderId = UUID.randomUUID();
        UUID shopId = UUID.randomUUID();

        Order order = mock(Order.class);
        given(order.getId()).willReturn(orderId);

        Review review = mock(Review.class);
        given(review.getReviewId()).willReturn(rid);
        given(review.getOrder()).willReturn(order);
        given(review.getUserId()).willReturn(author);
        given(review.getShopId()).willReturn(shopId);
        given(review.getContent()).willReturn("맛있어요");
        given(review.getRating()).willReturn(5);

        given(reviewRepository.findById(rid)).willReturn(Optional.of(review));

        ReviewResponse res = sut.getById(rid);

        assertThat(res).isNotNull();
    }

    @Test
    @DisplayName("getById: 없으면 REVIEW_NOT_FOUND")
    void getById_not_found() {
        UUID rid = UUID.randomUUID();
        given(reviewRepository.findById(rid)).willReturn(Optional.empty());

        assertThatThrownBy(() -> sut.getById(rid))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.REVIEW_NOT_FOUND);
    }

    @Test
    @DisplayName("getByShop: 페이지 조회 성공")
    void getByShop_ok() {
        UUID shopId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

        UUID o1 = UUID.randomUUID();
        Order ord1 = mock(Order.class);
        given(ord1.getId()).willReturn(o1);

        Review r1 = mock(Review.class);
        given(r1.getReviewId()).willReturn(UUID.randomUUID());
        given(r1.getOrder()).willReturn(ord1);
        given(r1.getUserId()).willReturn(1L);
        given(r1.getShopId()).willReturn(shopId);
        given(r1.getContent()).willReturn("맛1");
        given(r1.getRating()).willReturn(5);

        UUID o2 = UUID.randomUUID();
        Order ord2 = mock(Order.class);
        given(ord2.getId()).willReturn(o2);

        Review r2 = mock(Review.class);
        given(r2.getReviewId()).willReturn(UUID.randomUUID());
        given(r2.getOrder()).willReturn(ord2);
        given(r2.getUserId()).willReturn(2L);
        given(r2.getShopId()).willReturn(shopId);
        given(r2.getContent()).willReturn("맛2");
        given(r2.getRating()).willReturn(4);

        given(reviewRepository.findByShopId(eq(shopId), eq(pageable)))
                .willReturn(new PageImpl<>(List.of(r1, r2), pageable, 2));

        Page<ReviewResponse> page = sut.getByShop(shopId, pageable);

        assertThat(page).isNotNull();
        assertThat(page.getContent()).hasSize(2);
    }

    // ===== 업데이트 =====
    @Test
    @DisplayName("update: 작성자 아니면 FORBIDDEN_ACCESS")
    void update_forbidden() {
        UUID rid = UUID.randomUUID();

        Review review = mock(Review.class);
        given(review.getUserId()).willReturn(2L);

        given(reviewRepository.findById(rid)).willReturn(Optional.of(review));

        UpdateReviewRequest req = new UpdateReviewRequest("수정", 4);

        assertThatThrownBy(() -> sut.update(rid, 1L, req))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FORBIDDEN_ACCESS);
    }

    @Test
    @DisplayName("update: NOOP(content/rating 모두 null) → 도메인 메서드 호출 없음")
    void update_noop() {
        UUID rid = UUID.randomUUID();
        Long author = 1L;
        UUID orderId = UUID.randomUUID();
        UUID shopId = UUID.randomUUID();

        Order order = mock(Order.class);
        given(order.getId()).willReturn(orderId);

        Review review = mock(Review.class);
        given(review.getReviewId()).willReturn(rid);
        given(review.getOrder()).willReturn(order);
        given(review.getUserId()).willReturn(author);
        given(review.getShopId()).willReturn(shopId);
        given(review.getContent()).willReturn("원본 내용");
        given(review.getRating()).willReturn(4);
        given(review.getCreatedAt()).willReturn(java.time.LocalDateTime.now());
        given(review.getUpdatedAt()).willReturn(java.time.LocalDateTime.now());
        given(review.getCreatedBy()).willReturn(author);

        given(reviewRepository.findById(rid)).willReturn(java.util.Optional.of(review));

        UpdateReviewRequest req = new UpdateReviewRequest(null, null);

        ReviewResponse res = sut.update(rid, author, req);

        assertThat(res).isNotNull();
        then(review).should(never()).updateContent(anyString());
        then(review).should(never()).updateRating(anyInt());
    }

    @Test
    @DisplayName("update: content/rating 모두 변경 호출")
    void update_both() {
        UUID rid = UUID.randomUUID();
        Long author = 1L;
        UUID orderId = UUID.randomUUID();
        UUID shopId = UUID.randomUUID();

        Order order = mock(Order.class);
        given(order.getId()).willReturn(orderId);

        Review review = mock(Review.class);
        given(review.getUserId()).willReturn(author);
        given(review.getOrder()).willReturn(order);
        given(review.getShopId()).willReturn(shopId);
        given(review.getContent()).willReturn("원본");
        given(review.getRating()).willReturn(4);
        given(review.getCreatedAt()).willReturn(LocalDateTime.now());
        given(review.getUpdatedAt()).willReturn(LocalDateTime.now());
        given(review.getCreatedBy()).willReturn(author);

        given(reviewRepository.findById(rid)).willReturn(Optional.of(review));

        UpdateReviewRequest req = new UpdateReviewRequest("맛 최고", 5);

        ReviewResponse res = sut.update(rid, author, req);

        assertThat(res).isNotNull();
        then(review).should().updateContent("맛 최고");
        then(review).should().updateRating(5);
    }

    // ====== softDelete / restore ======
    @Test
    @DisplayName("softDelete: 작성자 아니면 FORBIDDEN_ACCESS")
    void softDelete_forbidden() {
        UUID rid = UUID.randomUUID();

        Review review = mock(Review.class);
        given(review.getUserId()).willReturn(2L);

        given(reviewRepository.findById(rid)).willReturn(Optional.of(review));

        assertThatThrownBy(() -> sut.softDelete(rid, 1L))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FORBIDDEN_ACCESS);
    }

    @Test
    @DisplayName("softDelete: 이미 삭제면 ALREADY_DELETED_REVIEW")
    void softDelete_already_deleted() {
        UUID rid = UUID.randomUUID();

        Review review = mock(Review.class);
        given(review.getUserId()).willReturn(1L);
        given(review.isDeleted()).willReturn(true);

        given(reviewRepository.findById(rid)).willReturn(Optional.of(review));

        assertThatThrownBy(() -> sut.softDelete(rid, 1L))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ALREADY_DELETED_REVIEW);
    }

    @Test
    @DisplayName("softDelete: 정상 삭제")
    void softDelete_ok() {
        UUID rid = UUID.randomUUID();

        Review review = mock(Review.class);
        given(review.getUserId()).willReturn(1L);
        given(review.isDeleted()).willReturn(false);
        given(reviewRepository.findById(rid)).willReturn(Optional.of(review));

        sut.softDelete(rid, 1L);

        then(review).should().softDelete(1L);
    }

    @Test
    @DisplayName("restore: 리뷰 없음 → REVIEW_NOT_FOUND")
    void restore_not_found() {
        UUID rid = UUID.randomUUID();
        given(reviewRepository.findByIdIncludingDeleted(rid)).willReturn(Optional.empty());

        assertThatThrownBy(() -> sut.restore(rid, 1L))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.REVIEW_NOT_FOUND);
    }

    @Test
    @DisplayName("restore: 작성자 아니면 FORBIDDEN_ACCESS")
    void restore_forbidden() {
        UUID rid = UUID.randomUUID();

        Review review = mock(Review.class);
        given(review.getUserId()).willReturn(2L);

        given(reviewRepository.findByIdIncludingDeleted(rid)).willReturn(Optional.of(review));

        assertThatThrownBy(() -> sut.restore(rid, 1L))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FORBIDDEN_ACCESS);
    }

    @Test
    @DisplayName("restore: 이미 활성 리뷰면 ALREADY_ACTIVE_REVIEW")
    void restore_already_active() {
        UUID rid = UUID.randomUUID();

        Review review = mock(Review.class);
        given(review.getUserId()).willReturn(1L);
        given(review.isDeleted()).willReturn(false);

        given(reviewRepository.findByIdIncludingDeleted(rid)).willReturn(Optional.of(review));

        assertThatThrownBy(() -> sut.restore(rid, 1L))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ALREADY_ACTIVE_REVIEW);
    }

    @Test
    @DisplayName("restore: 정상 복구")
    void restore_ok() {
        UUID rid = UUID.randomUUID();

        Review review = mock(Review.class);
        given(review.getUserId()).willReturn(1L);
        given(review.isDeleted()).willReturn(true);

        given(reviewRepository.findByIdIncludingDeleted(rid)).willReturn(Optional.of(review));

        sut.restore(rid, 1L);

        then(review).should().restore();
    }
}
