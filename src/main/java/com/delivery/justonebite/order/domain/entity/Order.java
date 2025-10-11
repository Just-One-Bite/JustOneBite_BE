package com.delivery.justonebite.order.domain.entity;

import com.delivery.justonebite.global.common.entity.BaseEntity;
import com.delivery.justonebite.global.exception.custom.CustomException;
import com.delivery.justonebite.global.exception.response.ErrorCode;
import com.delivery.justonebite.order.domain.enums.OrderStatus;
import com.delivery.justonebite.shop.domain.entity.Shop;
import com.delivery.justonebite.user.domain.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "h_order")
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    // 배달지 주소
    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "user_phonenumber", nullable = false, length = 15)
    private String userPhoneNumber;

    // 주문 요약 ex) 토스 티셔츠 외 2건
    @Column(name = "order_name", nullable = false)
    private String orderName;

    // 단품 개수
    @Column(name = "total_price", nullable = false)
    private Integer totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_status", nullable = false, length = 30)
    private OrderStatus currentStatus;

    // 주문 요청사항
    @Column(name = "order_request", length = 100)
    private String orderRequest;

    // 배달 요청사항
    @Column(name = "delivery_request", length = 100)
    private String deliveryRequest;

    private Order(User user, Shop shop, String address, String userPhoneNumber,
        String orderName, Integer totalPrice, OrderStatus currentStatus,
        String orderRequest, String deliveryRequest) {
        this.customer = user;
        this.shop = shop;
        this.address = address;
        this.userPhoneNumber = userPhoneNumber;
        this.orderName = orderName;
        this.currentStatus = currentStatus;
        this.totalPrice = totalPrice;
        this.orderRequest = orderRequest;
        this.deliveryRequest = deliveryRequest;
    }

    public void updateCurrentStatus(OrderStatus nextStatus) {
        getValidatedOrder(nextStatus);
        this.currentStatus = nextStatus;
    }

    private void getValidatedOrder(OrderStatus status) {
        // 주문 상태 전이 유효성 검증
        if (!this.currentStatus.isValidNextStatus(status)) {
            throw new CustomException(ErrorCode.INVALID_ORDER_STATUS);
        }
    }

    public static Order create(
        User user,
        Shop shop,
        String address,
        String userPhoneNumber,
        String orderName,
        Integer totalPrice,
        OrderStatus currentStatus,
        String orderRequest,
        String deliveryRequest
    ) {
        return new Order(
            user,
            shop,
            address,
            userPhoneNumber,
            orderName,
            totalPrice,
            currentStatus,
            orderRequest,
            deliveryRequest
        );
    }
}
