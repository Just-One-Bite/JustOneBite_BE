package com.delivery.justonebite.order.application.service;

import com.delivery.justonebite.order.domain.entity.Order;
import com.delivery.justonebite.order.domain.repository.OrderRepository;
import com.delivery.justonebite.order.presentation.dto.request.CreateOrderRequest;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    // TODO: User, Shop 정보 받아와야 함 (추후 더미 데이터 수정 필요)
    @Transactional
    public void createOrder(CreateOrderRequest request) {

        // TODO: Address 테이블에서 Id로 주소값 가져와야 함 (없으면 예외처리)
        String address = "서울시 종로구 사직로 155-2";

        // Item(상품) 테이블에서 Id로 객체 가져와야 함
        UUID itemId = request.orderItems().getFirst().itemId();
        int count = request.orderItems().size() - 1;
        String orderName = "마라탕 외 " + count + "건";
//        Item item = itemRepository.findById(itemId);
        Integer totalPrice = 15000;

        // 추후에 created_by에 userId 추가
        Order order = Order.create(address,
                request.userPhoneNumber(),
                orderName,
                totalPrice,
                request.orderRequest(),
                request.deliveryRequest()
            );

        orderRepository.save(order);
    }
}
