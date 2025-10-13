package com.delivery.justonebite.order.presentation.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.delivery.justonebite.global.common.security.UserDetailsImpl;
import com.delivery.justonebite.order.application.service.OrderService;
import com.delivery.justonebite.order.presentation.dto.request.CreateOrderRequest;
import com.delivery.justonebite.user.domain.entity.User;
import com.delivery.justonebite.user.domain.entity.UserRole;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = OrderController.class)
@AutoConfigureMockMvc(addFilters = true)
class OrderControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    OrderService orderService;

    @MockitoBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    private final Long USER_ID = 1L;
    private final UUID SHOP_ID = UUID.randomUUID();
    private final UUID ITEM_ID = UUID.randomUUID();
    private final UUID ORDER_ID = UUID.randomUUID();
    private final UUID ORDER_HISTORY_ID = UUID.randomUUID();

    private UsernamePasswordAuthenticationToken auth(Long userId, UserRole userRole) {
        UserDetailsImpl principal = Mockito.mock(UserDetailsImpl.class, Mockito.RETURNS_DEEP_STUBS);
        given(principal.getUserId()).willReturn(userId);
        given(principal.getUserRole()).willReturn(userRole);
        given(principal.getUser().getUserRole()).willReturn(userRole);

        String role = userRole.getRole();
        if (!role.startsWith("ROLE_")) role = "ROLE_" + role;

        return new UsernamePasswordAuthenticationToken(
            principal, "N/A", List.of(new SimpleGrantedAuthority(role))
        );
    }


    @Test
    @DisplayName("POST /v1/orders - 201 Created : 주문 완료되면 201 상태값 표시")
    void createOrder_Success_Returns_Created() throws Exception {

        willDoNothing().given(orderService).createOrder(any(CreateOrderRequest.class), any(User.class));

        String requestBody = """
                {
                  "shopId": "%s",
                  "deliveryAddressId": "123e4567-e89b-31d3-a456-426614174002",
                  "userPhoneNumber": "010-1254-5678",
                  "orderRequest": "단무지 빼주세요.",
                  "deliveryRequest": "문 앞에 놓아주세요",
                  "orderItems": [
                    {
                      "itemId": "%s",
                      "count": 1
                    },
                    {
                      "itemId": "%s",
                      "count": 1
                    }
                  ],
                  "totalPrice": 37000
                }
            """.formatted(SHOP_ID, ITEM_ID, ITEM_ID);

        mockMvc.perform(post("/v1/orders")
                .with(authentication(auth(USER_ID, UserRole.CUSTOMER)))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isCreated());
    }
}


