package com.delivery.justonebite.item.application.service;

import com.delivery.justonebite.ai_history.domain.repository.AiRequestHistoryRepository;
import com.delivery.justonebite.global.exception.custom.CustomException;
import com.delivery.justonebite.global.exception.response.ErrorCode;
import com.delivery.justonebite.item.domain.entity.Item;
import com.delivery.justonebite.item.domain.repository.ItemRepository;
import com.delivery.justonebite.item.infrastructure.api.gemini.client.GeminiClient;

import com.delivery.justonebite.item.presentation.dto.request.ItemRequest;
import com.delivery.justonebite.item.presentation.dto.request.ItemUpdateRequest;
import com.delivery.justonebite.item.presentation.dto.response.ItemDetailResponse;
import com.delivery.justonebite.item.presentation.dto.response.ItemResponse;
import com.delivery.justonebite.shop.domain.repository.ShopRepository;
import com.delivery.justonebite.user.domain.entity.UserRole;
import com.delivery.justonebite.user.domain.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
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
class ItemServiceCustomerTest {
    private final Long CUSTOMER_ID = 2L;

    private UserRepository userRepository;
    private ItemRepository itemRepository;
    private ShopRepository shopRepository;
    private AiRequestHistoryRepository aiRequestHistoryRepository;
    private GeminiClient geminiClient;

    private ItemService itemService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        itemRepository = mock(ItemRepository.class);
        shopRepository = mock(ShopRepository.class);
        aiRequestHistoryRepository = mock(AiRequestHistoryRepository.class);
        geminiClient = mock(GeminiClient.class);
        itemService = new ItemService(userRepository, itemRepository, shopRepository, geminiClient, aiRequestHistoryRepository);

        SecurityContextHolder.getContext().setAuthentication(auth(CUSTOMER_ID, UserRole.CUSTOMER));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private UsernamePasswordAuthenticationToken auth(Long userId, UserRole userRole) {
        String role = userRole.getRole();
        if (!role.startsWith("ROLE_")) role = "ROLE_" + role;

        return new UsernamePasswordAuthenticationToken(
            userId, "N/A", List.of(new SimpleGrantedAuthority(role))
        );
    }

    @Test
    @DisplayName("createItem : 상품 등록 + 권한 없음")
    void createItem() {
        UUID shopId = UUID.randomUUID();

        ItemRequest itemRequest = ItemRequest.builder()
            .shopId(String.valueOf(shopId))
            .name("김치찜")
            .price(15000)
            .description("맛있는 김치찜")
            .aiGenerated(false)
            .build();

        assertThatThrownBy(() -> itemService.createItem(CUSTOMER_ID, UserRole.CUSTOMER, itemRequest))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FORBIDDEN_ACCESS);
    }

    @Test
    @DisplayName("get item from owner : 권한 없음")
    void getItemFromOwner() {
        UUID itemId = UUID.randomUUID();

        assertThatThrownBy(() -> itemService.getItemFromOwner(CUSTOMER_ID, UserRole.CUSTOMER, itemId))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FORBIDDEN_ACCESS);
    }

    @Test
    @DisplayName("get item from customer : 아이템 조회 성공")
    void getItemFromCustomer() {
        UUID itemId = UUID.randomUUID();
        given(itemRepository.findByItemIdWithoutHidden(itemId)).willReturn(Optional.of(mock(Item.class)));

        ItemDetailResponse res = itemService.getItemFromCustomer(itemId);

        assertThat(res).isNotNull();
    }

    @Test
    @DisplayName("get items from owner : 권한 없음")
    void getItemsByShopFromOwner() {
        UUID shopId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

        assertThatThrownBy(() -> itemService.getItemsByShopFromOwner(CUSTOMER_ID, UserRole.CUSTOMER, shopId, pageable))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FORBIDDEN_ACCESS);
    }

    @Test
    @DisplayName("get items from customer : 페이지 조회 성공")
    void getItemsByShopFromCustomer() {
        UUID shopId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

        Item i1 = mock(Item.class);
        Item i2 = mock(Item.class);

        given(itemRepository.findAllByShopIdWithoutHidden(shopId, pageable))
            .willReturn(new PageImpl<>(List.of(i1, i2)));

        Page<ItemResponse> res = itemService.getItemsByShopFromCustomer(shopId, pageable);
        assertThat(res).isNotNull();
        assertThat(res.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("updateItem : 상품 수정 + 권한 없음")
    void updateItem() {
        UUID itemId = UUID.randomUUID();

        ItemUpdateRequest itemRequest = new ItemUpdateRequest(
            "김치찜",
            15000,
            "image",
            "맛있는 김치찜",
            false
        );

        assertThatThrownBy(() -> itemService.updateItem(CUSTOMER_ID, UserRole.CUSTOMER, itemId, itemRequest))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FORBIDDEN_ACCESS);
    }

    @Test
    @DisplayName("softDelete : 상품 삭제 + 권한 없음")
    void softDelete() {
        assertThatThrownBy(() -> itemService.softDelete(CUSTOMER_ID, UserRole.CUSTOMER, UUID.randomUUID()))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FORBIDDEN_ACCESS);
    }

    @Test
    @DisplayName("restoreItem : 상품 복구 + 권한 없음")
    void restoreItem() {
        assertThatThrownBy(() -> itemService.restoreItem(CUSTOMER_ID, UserRole.CUSTOMER, UUID.randomUUID()))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FORBIDDEN_ACCESS);
    }

    @Test
    @DisplayName("toggleHidden : 상품 숨김/해제 + 권한 없음")
    void toggleHidden() {
        assertThatThrownBy(() -> itemService.toggleHidden(CUSTOMER_ID, UserRole.CUSTOMER, UUID.randomUUID()))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FORBIDDEN_ACCESS);
    }
}