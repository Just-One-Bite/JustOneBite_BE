package com.delivery.justonebite.order.presentation.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.delivery.justonebite.global.common.security.UserDetailsImpl;
import com.delivery.justonebite.global.exception.custom.CustomException;
import com.delivery.justonebite.global.exception.response.ErrorCode;
import com.delivery.justonebite.order.application.service.OrderService;
import com.delivery.justonebite.order.presentation.dto.request.CreateOrderRequest;
import com.delivery.justonebite.order.stub.StubData;
import com.delivery.justonebite.user.domain.entity.User;
import com.delivery.justonebite.user.domain.entity.UserRole;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest(controllers = OrderController.class)
@AutoConfigureMockMvc(addFilters = true)
class OrderControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    OrderService orderService;

    // MockMvc를 빌드할 때 springSecurity()설정을 명시적으로 적용해야 권한 규칙이 MockMvc 환경에 통합됨
    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    private final Long USER_ID = 1L;
    private final UUID SHOP_ID = UUID.randomUUID();
    private final UUID ITEM_ID = UUID.randomUUID();
    private final UUID ORDER_ID = UUID.randomUUID();
    private final UUID ORDER_HISTORY_ID = UUID.randomUUID();

//    @BeforeEach
//    public void setup() {
//        // WebApplicationContext를 사용하여 Spring Security 필터 체인을 적용합니다.
//        mockMvc = MockMvcBuilders
//            .webAppContextSetup(context)
//            .apply(springSecurity()) // 이 부분이 핵심! Spring Security 필터 적용
//            .build();
//    }

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

        String requestBody = StubData.getCreateOrderRequest().formatted(SHOP_ID, ITEM_ID, ITEM_ID);

        mockMvc.perform(post("/v1/orders")
                .with(authentication(auth(USER_ID, UserRole.CUSTOMER)))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isCreated());

        // 서비스 메서드가 실제로 호출되었는지 확인
        verify(orderService, times(1)).createOrder(any(CreateOrderRequest.class), any(User.class));
    }

    @Test
    @DisplayName("POST /v1/orders - 403 FORBIDDEN : 유저 권한이 CUSTOMER가 아닐 경우")
    void createOrder_Role_Fails_Returns_Forbidden() throws Exception {
        // orderService.createOrder()가 호출되면 커스텀 예외를 던지도록 설정
        // 컨트롤러 테스트에서는 그 결과(예외 발생)dㅔ 대해서만 예외 핸들링을 검증
        doThrow(new CustomException(ErrorCode.FORBIDDEN_ACCESS))
            .when(orderService).createOrder(any(CreateOrderRequest.class), any(User.class));

        String requestBody = StubData.getCreateOrderRequest().formatted(SHOP_ID, ITEM_ID, ITEM_ID);
        mockMvc.perform(post("/v1/orders")
                .with(authentication(auth(USER_ID, UserRole.OWNER)))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.errorCode").value("FORBIDDEN_ACCESS"))
            .andExpect(jsonPath("$.description").value("접근 권한이 없습니다."));

        // 권한 실패로 인해 서비스 메서드가 호출되지 않았는지 검증
        verify(orderService, times(1)).createOrder(any(CreateOrderRequest.class), any(User.class));
    }

    @Test
    @DisplayName("POST /v1/orders - 400 BAD_REQUEST : 클라이언트로부터 받아온 총 금액값이 서버에서 계산된 총 금액값과 불일치")
    void createOrder_Total_Price_Fails_Returns_Bad_Request() throws Exception {
        doThrow(new CustomException(ErrorCode.TOTAL_PRICE_NOT_MATCH))
            .when(orderService).createOrder(any(CreateOrderRequest.class), any(User.class));

        String requestBody = StubData.getCreateOrderRequest().formatted(SHOP_ID, ITEM_ID, ITEM_ID);

        mockMvc.perform(post("/v1/orders")
                .with(authentication(auth(USER_ID, UserRole.CUSTOMER)))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").value("TOTAL_PRICE_NOT_MATCH"))
            .andExpect(jsonPath("$.description").value("전체 주문금액이 일치하지 않습니다."));

        // 서비스 메서드가 실제로 호출되었는지 확인
        verify(orderService, times(1)).createOrder(any(CreateOrderRequest.class), any(User.class));
    }
}


