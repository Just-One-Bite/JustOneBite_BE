package com.delivery.justonebite.ai_history.presentation.controller;

import com.delivery.justonebite.ai_history.application.service.AiRequestHistoryService;
import com.delivery.justonebite.ai_history.presentation.dto.AiRequestHistoryResponse;
import com.delivery.justonebite.global.config.security.UserDetailsImpl;
import com.delivery.justonebite.item.presentation.controller.ItemController;
import com.delivery.justonebite.user.domain.entity.User;
import com.delivery.justonebite.user.domain.entity.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AiRequestHistoryController.class)
@AutoConfigureMockMvc
class AiRequestHistoryControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    AiRequestHistoryService aiRequestHistoryService;

    @MockitoBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    private final Long USER_ID = 1L;
    private final UUID HISTORY_ID = UUID.randomUUID();

    private UsernamePasswordAuthenticationToken auth(Long userId, UserRole userRole) {
        UserDetailsImpl principal = mock(UserDetailsImpl.class, Mockito.RETURNS_DEEP_STUBS);
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
    @DisplayName("GET /v1/ai - 200 Created + ItemResponse 필드 검증")
    void getHistory() throws Exception {
        var res = AiRequestHistoryResponse.builder()
            .id(HISTORY_ID)
            .model("model")
            .request("request")
            .response("response")
            .createdAt(LocalDateTime.now())
            .build();

        given(aiRequestHistoryService.getHistory(USER_ID, HISTORY_ID))
            .willReturn(res);

        mvc.perform(get("/v1/ai/{id}", HISTORY_ID)
                .with(authentication(auth(USER_ID, UserRole.OWNER))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(HISTORY_ID.toString()))
            .andExpect(jsonPath("$.model").value("model"))
            .andExpect(jsonPath("$.request").value("request"))
            .andExpect(jsonPath("$.response").value("response"));
    }

    @Test
    @DisplayName("GET /v1/ai - 200 Created + ItemResponse 필드 검증")
    void getHistories() throws Exception {
        given(aiRequestHistoryService.getHistories(any(User.class), any()))
            .willReturn(Page.empty());

        mvc.perform(get("/v1/ai")
                .with(authentication(auth(USER_ID, UserRole.OWNER)))
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk());
    }
}