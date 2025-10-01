package com.delivery.justonebite.order.domain.entity;

import com.delivery.justonebite.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

@Entity
@Table(name = "h_order")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_id")
    private UUID id;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id")
//    private User customer;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "shop_id")
//    private Shop shop;

    // 배달지 주소
    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "user_phonenumber", nullable = false)
    private String userPhoneNumber;

    // 주문 요약 ex) 토스 티셔츠 외 2건
    @Column(name = "order_name", nullable = false)
    private String orderName;

    // 단품 개수
    @Column(name = "total_price", nullable = false)
    private Integer totalPrice;

    // 주문 요청사항
    @Column(name = "order_request")
    private String orderRequest;

    // 배달 요청사항
    @Column(name = "delivery_request")
    private String deliveryRequest;

    private Order(String address, String userPhoneNumber,
        String orderName, Integer totalPrice,
        String orderRequest, String deliveryRequest) {
        this.address = address;
        this.userPhoneNumber = userPhoneNumber;
        this.orderName = orderName;
        this.totalPrice = totalPrice;
        this.orderRequest = orderRequest;
        this.deliveryRequest = deliveryRequest;
    }

    // TODO: User, Shop 객체 넘기기
    public static Order create(String address,
        String userPhoneNumber,
        String orderName,
        Integer totalPrice,
        String orderRequest,
        String deliveryRequest
    ) {
        return new Order(
            address,
            userPhoneNumber,
            orderName,
            totalPrice,
            orderRequest,
            deliveryRequest
        );
    }
}
