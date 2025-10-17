package com.delivery.justonebite.item.presentation.controller;

import com.delivery.justonebite.global.common.security.UserDetailsImpl;
import com.delivery.justonebite.item.application.service.ItemService;

import com.delivery.justonebite.item.presentation.dto.response.ItemDetailResponse;
import com.delivery.justonebite.item.presentation.dto.response.ItemOwnerDetailResponse;
import com.delivery.justonebite.item.presentation.dto.response.ItemResponse;
import com.delivery.justonebite.user.domain.entity.UserRole;
import org.hamcrest.Matchers;
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
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(controllers = ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    ItemService itemService;

    @MockitoBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    private final Long USER_ID = 1L;
    private final UUID ITEM_ID = UUID.randomUUID();
    private final UUID SHOP_ID = UUID.randomUUID();

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
    @DisplayName("POST /v1/items - 200 Created + ItemResponse 필드 검증")
    void createItem() throws Exception {
        var resp = new ItemResponse(
            ITEM_ID,
            "김치찜",
            15000,
            "image"
        );

        given(itemService.createItem(any(Long.class), any(UserRole.class), any()))
            .willReturn(resp);

        String body = """
                {
                  "shop_id": "%s",
                  "name": "김치찜",
                  "price": 15000,
                  "image": "image",
                  "description": "맛있는 김치찜",
                  "ai_generated": false
                }
                """.formatted(SHOP_ID);

        mvc.perform(post("/v1/items")
                .with(authentication(auth(USER_ID, UserRole.OWNER)))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.itemId").value(ITEM_ID.toString()))
            .andExpect(jsonPath("$.name").value("김치찜"))
            .andExpect(jsonPath("$.price").value(15000))
            .andExpect(jsonPath("$.image").value("image"));
    }

    @Test
    @DisplayName("GET /v1/items/owner/{id} - 200 OK + ItemOwnerDetailResponse 필드 검증")
    void getItemFromOwner() throws Exception {
        var now = LocalDateTime.now();

        var resp = new ItemOwnerDetailResponse(
            ITEM_ID,
            "김치찜",
            15000,
            "image",
            "맛있는 김치찜",
            false,
            now,
            1L,
            now,
            1L,
            null,
            null
        );

        given(itemService.getItemFromOwner(any(Long.class), any(UserRole.class), eq(ITEM_ID)))
            .willReturn(resp);


        mvc.perform(get("/v1/items/owner/{id}", ITEM_ID)
                .with(authentication(auth(USER_ID, UserRole.OWNER))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.itemId").value(ITEM_ID.toString()))
            .andExpect(jsonPath("$.name").value("김치찜"))
            .andExpect(jsonPath("$.price").value(15000))
            .andExpect(jsonPath("$.image").value("image"))
            .andExpect(jsonPath("$.description").value("맛있는 김치찜"))
            .andExpect(jsonPath("$.isHidden").value(false))
            .andExpect(jsonPath("$.createdBy").value(USER_ID.toString()))
            .andExpect(jsonPath("$.updatedBy").value(USER_ID.toString()))
            .andExpect(jsonPath("$.deletedAt").value(Matchers.nullValue()))
            .andExpect(jsonPath("$.deletedBy").value(Matchers.nullValue()));
    }

    @Test
    @DisplayName("GET /v1/items/{id} - 200 OK + ItemDetailResponse 필드 검증")
    void getItemFromCustomer() throws Exception {
        var resp = new ItemDetailResponse(
            ITEM_ID,
            "김치찜",
            15000,
            "image",
            "맛있는 김치찜"
        );

        given(itemService.getItemFromCustomer(eq(ITEM_ID)))
            .willReturn(resp);


        mvc.perform(get("/v1/items/{id}", ITEM_ID)
                .with(authentication(auth(USER_ID, UserRole.CUSTOMER))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.itemId").value(ITEM_ID.toString()))
            .andExpect(jsonPath("$.name").value("김치찜"))
            .andExpect(jsonPath("$.price").value(15000))
            .andExpect(jsonPath("$.image").value("image"))
            .andExpect(jsonPath("$.description").value("맛있는 김치찜"));
    }

    @Test
    @DisplayName("GET /v1/items/owner/shop/{id} - 200 OK + ItemOwnerDetailResponse 필드 검증")
    void getItemsByShopFromOwner() throws Exception {
        given(itemService.getItemsByShopFromOwner(any(Long.class), any(UserRole.class), eq(SHOP_ID), any()))
            .willReturn(org.springframework.data.domain.Page.empty());

        mvc.perform(get("/v1/items/owner/shop/{id}", SHOP_ID)
            .with(authentication(auth(USER_ID, UserRole.OWNER)))
            .param("page", "0")
            .param("size", "10"))
        .andExpect(status().isOk());
    }

    @Test
    void getItemsByShopFromCustomer() throws Exception {
        given(itemService.getItemsByShopFromCustomer(eq(SHOP_ID), any()))
            .willReturn(org.springframework.data.domain.Page.empty());

        mvc.perform(get("/v1/items/shop/{id}", SHOP_ID)
                .with(authentication(auth(USER_ID, UserRole.CUSTOMER)))
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /v1/items/{id} - 200 OK + ItemResponse 필드 검증")
    void updateItem() throws Exception {
        var resp = new ItemResponse(
            ITEM_ID,
            "김치찜",
            15000,
            "image"
        );

        given(itemService.updateItem(any(Long.class), any(UserRole.class), eq(ITEM_ID), any()))
            .willReturn(resp);

        String body = """
                {
                  "name": "김치찜",
                  "price": 15000,
                  "image": "image",
                  "description": "맛있는 김치찜",
                  "ai_generated": false
                }
                """;

        mvc.perform(put("/v1/items/{id}", ITEM_ID)
                .with(authentication(auth(USER_ID, UserRole.OWNER)))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.itemId").value(ITEM_ID.toString()))
            .andExpect(jsonPath("$.name").value("김치찜"))
            .andExpect(jsonPath("$.price").value(15000))
            .andExpect(jsonPath("$.image").value("image"));
    }

    @Test
    @DisplayName("DELETE /v1/items/{id} - 200 OK 검증")
    void softDelete() throws Exception {
        willDoNothing().given(itemService).softDelete(any(Long.class), any(UserRole.class), eq(ITEM_ID));

        mvc.perform(delete("/v1/items/{id}", ITEM_ID)
                .with(authentication(auth(USER_ID, UserRole.OWNER)))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /v1/items/{id}/restore - 200 OK 검증")
    void restoreItem() throws Exception {
        willDoNothing().given(itemService).softDelete(any(Long.class), any(UserRole.class), eq(ITEM_ID));

        mvc.perform(patch("/v1/items/{id}/restore", ITEM_ID)
                .with(authentication(auth(USER_ID, UserRole.OWNER)))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /v1/items/{id}/hide - 200 OK 검증")
    void toggleHidden() throws Exception {
        willDoNothing().given(itemService).softDelete(any(Long.class), any(UserRole.class), eq(ITEM_ID));

        mvc.perform(patch("/v1/items/{id}/hide", ITEM_ID)
                .with(authentication(auth(USER_ID, UserRole.OWNER)))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }
}