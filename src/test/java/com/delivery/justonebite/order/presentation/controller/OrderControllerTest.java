package com.delivery.justonebite.order.presentation.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.delivery.justonebite.global.common.security.UserDetailsImpl;
import com.delivery.justonebite.global.exception.custom.CustomException;
import com.delivery.justonebite.global.exception.response.ErrorCode;
import com.delivery.justonebite.order.application.service.OrderService;
import com.delivery.justonebite.order.domain.enums.OrderStatus;
import com.delivery.justonebite.order.presentation.dto.request.CreateOrderRequest;
import com.delivery.justonebite.order.presentation.dto.response.CustomerOrderResponse;
import com.delivery.justonebite.order.presentation.dto.response.OrderDetailsResponse;
import com.delivery.justonebite.order.stub.StubData;
import com.delivery.justonebite.user.domain.entity.User;
import com.delivery.justonebite.user.domain.entity.UserRole;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.data.querydsl.QPageRequest;
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
        if (!role.startsWith("ROLE_"))
            role = "ROLE_" + role;

        return new UsernamePasswordAuthenticationToken(
            principal, "N/A", List.of(new SimpleGrantedAuthority(role))
        );
    }

    /**
     * 주문 생성
     */
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
            .andExpect(jsonPath("$.description").value("접근 권한이 없습니다."))
            .andExpect(jsonPath("$.status").value(403));

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

    /**
     * 주문 목록 조회
     */
    @Test
    @DisplayName("GET /v1/orders - 200 OK : CUSTOMER의 주문 목록 정상 조회되면 200 상태값 표시")
    void getCustomerOrders_Success_Returns_Ok() throws Exception {
        List<CustomerOrderResponse> content = StubData.getCustomerOrderResponse(ORDER_ID);

        // 페이지 mock
        Page<CustomerOrderResponse> mockPage = new PageImpl<>(content,
            PageRequest.of(0, 10, Sort.by("createdAt")), 100);

        // getCustomerOrders가 호출될 때 mockPage를 반환하도록 설정
        given(orderService.getCustomerOrders(anyInt(), anyInt(), anyString(), any(User.class)))
            .willReturn(mockPage);

        mockMvc.perform(get("/v1/orders")
                .with(authentication(auth(USER_ID, UserRole.CUSTOMER)))
                .contentType(MediaType.APPLICATION_JSON)
                .param("page", "1")
                .param("size", "10")
                .param("sort-by", "createdAt"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(content.size()))
            .andExpect(jsonPath("$.totalPages").value(10))
            .andExpect(jsonPath("$.totalElements").value(100))
            .andExpect(jsonPath("$.content[0].orderId").value(ORDER_ID.toString()))
            .andExpect(jsonPath("$.content[0].shopName").value("마라탕웨이"));
    }

    @Test
    @DisplayName("GET /v1/orders - 403 FORBIDDEN : 유저 권한이 CUSTOMER가 아닐 경우")
    void getCustomerOrders_Fails_Returns_Forbidden() throws Exception {
        doThrow(new CustomException(ErrorCode.FORBIDDEN_ACCESS))
            .when(orderService).getCustomerOrders(anyInt(), anyInt(), anyString(), any(User.class));

        mockMvc.perform(get("/v1/orders")
                .with(authentication(auth(USER_ID, UserRole.CUSTOMER)))
                .contentType(MediaType.APPLICATION_JSON)
                .param("page", "1")
                .param("size", "10")
                .param("sort-by", "createdAt"))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.errorCode").value("FORBIDDEN_ACCESS"))
            .andExpect(jsonPath("$.description").value("접근 권한이 없습니다."))
            .andExpect(jsonPath("$.status").value(403));
    }

    /**
     * 주문 상세정보 조회
     */
    @Test
    @DisplayName("GET /v1/orders/{order-id} - 200 OK : 주문 상세정보 정상 조회되면 200 상태값 표시")
    void getOrderDetails_Success_Returns_Ok() throws Exception {
        OrderDetailsResponse content = StubData.getOrderDetailsResponse(ORDER_ID, USER_ID, SHOP_ID);

        given(orderService.getOrderDetails(eq(ORDER_ID), any(User.class)))
            .willReturn(content);

        mockMvc.perform(get("/v1/orders/{order-id}", ORDER_ID)
                .with(authentication(auth(USER_ID, UserRole.CUSTOMER)))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.orderId").value(ORDER_ID.toString()))
            .andExpect(jsonPath("$.orderItems.length()").value(2))
            .andExpect(jsonPath("$.orderInfo.address").value("서울시 종로구 사직로 125길 00빌딩"))
            .andExpect(jsonPath("$.orderInfo.orderRequest").value("단무지 빼주세요"))
            .andExpect(jsonPath("$.orderItems[0].price").value(20000));
    }

    @Test
    @DisplayName("POST /v1/orders/{order-id} - 404 NOT_FOUND : 주문을 찾을 수 없는 경우")
    void getOrderDetails_Fails_Returns_Not_Found() throws Exception {
        doThrow(new CustomException(ErrorCode.ORDER_NOT_FOUND))
            .when(orderService).getOrderDetails(eq(ORDER_ID), any(User.class));

        mockMvc.perform(get("/v1/orders/{order-id}", ORDER_ID)
                .with(authentication(auth(USER_ID, UserRole.CUSTOMER)))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.errorCode").value("ORDER_NOT_FOUND"))
            .andExpect(jsonPath("$.description").value("주문을 찾을 수 없습니다."))
            .andExpect(jsonPath("$.status").value(404));
    }
}