package com.delivery.justonebite.ai_history.application.service;


import com.delivery.justonebite.ai_history.domain.entity.AiRequestHistory;
import com.delivery.justonebite.ai_history.domain.repository.AiRequestHistoryRepository;
import com.delivery.justonebite.ai_history.presentation.dto.AiRequestHistoryResponse;
import com.delivery.justonebite.global.exception.custom.CustomException;
import com.delivery.justonebite.user.domain.entity.User;
import com.delivery.justonebite.user.domain.entity.UserRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class AiRequestHistoryServiceTest {

    private final long USER_ID = 1L;
    private final long ANOTHER_USER_ID = 2L;

    private AiRequestHistoryRepository aiRequestHistoryRepository;

    private AiRequestHistoryService aiRequestHistoryService;


    @BeforeEach
    void setUp() {
        aiRequestHistoryRepository = mock(AiRequestHistoryRepository.class);
        aiRequestHistoryService = new AiRequestHistoryService(aiRequestHistoryRepository);

        SecurityContextHolder.getContext().setAuthentication(auth(USER_ID, UserRole.CUSTOMER));
    }

    private UsernamePasswordAuthenticationToken auth(Long userId, UserRole userRole) {
        String role = userRole.getRole();
        if (!role.startsWith("ROLE_")) role = "ROLE_" + role;

        return new UsernamePasswordAuthenticationToken(
            userId, "N/A", List.of(new SimpleGrantedAuthority(role))
        );
    }

    private User mockUser(Long userId) {
        User user = mock(User.class);
        given(user.getId()).willReturn(userId);

        return user;
    }

    private AiRequestHistory mockHistory(User user) {
        AiRequestHistory history = mock(AiRequestHistory.class);
        given(history.getUser())
            .willReturn(user);

        return history;
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("get History : 조회 실패")
    void getHistoryFromAnotherUser() {
        UUID id = UUID.randomUUID();
        User user = mockUser(USER_ID);
        AiRequestHistory history = mockHistory(user);
        given(aiRequestHistoryRepository.findById(id)).willReturn(Optional.of(history));

        assertThatThrownBy(() -> aiRequestHistoryService.getHistory(ANOTHER_USER_ID, id))
            .isInstanceOf(CustomException.class)
            .hasMessageContaining("접근 권한이 없습니다.");
    }

    @Test
    @DisplayName("get History : 조회 성공")
    void getHistoryFromOwner() {
        UUID id = UUID.randomUUID();
        User user = mockUser(USER_ID);
        AiRequestHistory history = mockHistory(user);
        given(aiRequestHistoryRepository.findById(id)).willReturn(Optional.of(history));

        AiRequestHistoryResponse res = aiRequestHistoryService.getHistory(USER_ID, id);

        assertThat(res).isNotNull();
    }

    @Test
    @DisplayName("get History : 조회 성공")
    void getHistories() {
        User user = mock(User.class);

        AiRequestHistory history1 = mock(AiRequestHistory.class);
        AiRequestHistory history2 = mock(AiRequestHistory.class);

        Pageable pageable = PageRequest.of(0, 10);

        given(aiRequestHistoryRepository.findAllByUser(user, pageable))
            .willReturn(new PageImpl<>(List.of(history1, history2)));

        Page<AiRequestHistoryResponse> res = aiRequestHistoryService.getHistories(user, pageable);

        assertThat(res).isNotNull();
        assertThat(res.getContent()).hasSize(2);
    }
}