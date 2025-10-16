package com.delivery.justonebite.review.presentation.controller;

import com.delivery.justonebite.global.config.security.UserDetailsImpl;
import com.delivery.justonebite.review.application.service.ReviewService;
import com.delivery.justonebite.review.presentation.dto.response.CreateReviewResponse;
import com.delivery.justonebite.review.presentation.dto.response.ReviewResponse;
import com.delivery.justonebite.user.domain.entity.UserRole;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ReviewController.class)
@AutoConfigureMockMvc(addFilters = true)
class ReviewControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    ReviewService reviewService;

    @MockitoBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    private final Long USER_ID = 1L;
    private final UUID REVIEW_ID = UUID.randomUUID();
    private final UUID SHOP_ID = UUID.randomUUID();
    private final UUID ORDER_ID = UUID.randomUUID();


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
    @DisplayName("POST /v1/reviews - 201 Created + CreateReviewResponse 필드 검증")
    void createReview_created() throws Exception {
        var now = LocalDateTime.now();

        var resp = new CreateReviewResponse(
                REVIEW_ID,
                ORDER_ID,
                SHOP_ID,
                "맛있어요!",
                5,
                now,
                USER_ID
        );

        given(reviewService.createReview(any(Long.class), any(UserRole.class), any()))
                .willReturn(resp);

        String body = """
                {
                  "orderId": "%s",
                  "shopId":  "%s",
                  "rating":  5,
                  "content": "맛있어요!"
                }
                """.formatted(ORDER_ID, SHOP_ID);

        mvc.perform(post("/v1/reviews")
                        .with(authentication(auth(USER_ID, UserRole.CUSTOMER)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reviewId").value(REVIEW_ID.toString()))
                .andExpect(jsonPath("$.orderId").value(ORDER_ID.toString()))
                .andExpect(jsonPath("$.shopId").value(SHOP_ID.toString()))
                .andExpect(jsonPath("$.content").value("맛있어요!"))
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.createdBy").value(USER_ID.intValue()));
    }

    @Test
    @DisplayName("GET /v1/reviews/{id} - 200 OK + ReviewResponse 필드 검증")
    void getOne_ok() throws Exception {
        var now = LocalDateTime.now();

        var resp = new ReviewResponse(
                REVIEW_ID,
                ORDER_ID,
                USER_ID,
                SHOP_ID,
                "좋아요",
                5,
                now,
                now,
                USER_ID
        );

        given(reviewService.getById(eq(REVIEW_ID))).willReturn(resp);

        mvc.perform(get("/v1/reviews/{id}", REVIEW_ID)
                        .with(authentication(auth(USER_ID, UserRole.CUSTOMER))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewId").value(REVIEW_ID.toString()))
                .andExpect(jsonPath("$.orderId").value(ORDER_ID.toString()))
                .andExpect(jsonPath("$.userId").value(USER_ID.intValue()))
                .andExpect(jsonPath("$.shopId").value(SHOP_ID.toString()))
                .andExpect(jsonPath("$.content").value("좋아요"))
                .andExpect(jsonPath("$.rating").value(5));
    }

    @Test
    @DisplayName("GET /v1/reviews?shopId= - 200 OK (Page 최소 검증)")
    void getByShop_ok() throws Exception {
        given(reviewService.getByShop(eq(SHOP_ID), any()))
                .willReturn(org.springframework.data.domain.Page.empty());

        mvc.perform(get("/v1/reviews")
                        .with(authentication(auth(USER_ID, UserRole.CUSTOMER)))
                        .param("shopId", SHOP_ID.toString())
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /v1/reviews/{id} - 200 OK + 수정된 ReviewResponse 필드 검증")
    void update_ok() throws Exception {
        var now = LocalDateTime.now();

        var resp = new ReviewResponse(
                REVIEW_ID,
                ORDER_ID,
                USER_ID,
                SHOP_ID,
                "괜찮아요",
                4,
                now.minusDays(1),
                now,
                USER_ID
        );

        given(reviewService.update(eq(REVIEW_ID), any(Long.class), any(UserRole.class), any()))
                .willReturn(resp);

        String body = """
                { "rating": 4, "content": "괜찮아요" }
                """;

        mvc.perform(patch("/v1/reviews/{id}", REVIEW_ID)
                        .with(authentication(auth(USER_ID, UserRole.CUSTOMER)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewId").value(REVIEW_ID.toString()))
                .andExpect(jsonPath("$.orderId").value(ORDER_ID.toString()))
                .andExpect(jsonPath("$.userId").value(USER_ID.intValue()))
                .andExpect(jsonPath("$.shopId").value(SHOP_ID.toString()))
                .andExpect(jsonPath("$.content").value("괜찮아요"))
                .andExpect(jsonPath("$.rating").value(4));
    }

    @Test
    @DisplayName("DELETE /v1/reviews/{id} - 200 OK + reviewId 반환")
    void softDelete_ok() throws Exception {
        mvc.perform(delete("/v1/reviews/{id}", REVIEW_ID)
                        .with(authentication(auth(USER_ID, UserRole.CUSTOMER)))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewId").value(REVIEW_ID.toString()));
    }

    @Test
    @DisplayName("POST /v1/reviews/{id}/restore - 200 OK + reviewId 반환")
    void restore_ok() throws Exception {
        mvc.perform(post("/v1/reviews/{id}/restore", REVIEW_ID)
                        .with(authentication(auth(USER_ID, UserRole.OWNER)))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewId").value(REVIEW_ID.toString()));
    }
}
