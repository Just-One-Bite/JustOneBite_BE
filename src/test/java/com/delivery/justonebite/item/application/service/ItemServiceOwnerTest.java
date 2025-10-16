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
import com.delivery.justonebite.item.presentation.dto.response.ItemOwnerDetailResponse;
import com.delivery.justonebite.item.presentation.dto.response.ItemResponse;
import com.delivery.justonebite.shop.domain.entity.Shop;
import com.delivery.justonebite.shop.domain.repository.ShopRepository;
import com.delivery.justonebite.user.domain.entity.UserRole;
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
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ItemServiceOwnerTest {

    private final Long OWNER_ID = 1L;
    private final Long ANOTHER_OWNER_ID = 2L;

    private ItemRepository itemRepository;
    private ShopRepository shopRepository;
    private AiRequestHistoryRepository aiRequestHistoryRepository;
    private GeminiClient geminiClient;

    private ItemService itemService;

    @BeforeEach
    void setUp() {
        itemRepository = mock(ItemRepository.class);
        shopRepository = mock(ShopRepository.class);
        aiRequestHistoryRepository = mock(AiRequestHistoryRepository.class);
        geminiClient = mock(GeminiClient.class);
        itemService = new ItemService(itemRepository, shopRepository, geminiClient, aiRequestHistoryRepository);

        SecurityContextHolder.getContext().setAuthentication(auth(OWNER_ID, UserRole.OWNER));
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

    private Shop mockShop(UUID shopId) {
        Shop shop = mock(Shop.class);
        given(shop.getOwnerId()).willReturn(OWNER_ID);

        return shop;
    }

    @Test
    @DisplayName("createItem : 가게 주인 불일치")
    void createItemFromAnotherOwner() {
        UUID shopId = UUID.randomUUID();

        Shop shop = mockShop(shopId);
        given(shopRepository.findById(shopId))
            .willReturn(Optional.of(shop));

        ItemRequest itemRequest = ItemRequest.builder()
            .shopId(String.valueOf(shopId))
            .name("김치찜")
            .price(15000)
            .description("맛있는 김치찜")
            .aiGenerated(false)
            .build();

        assertThatThrownBy(() -> itemService.createItem(ANOTHER_OWNER_ID, UserRole.OWNER, itemRequest))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FORBIDDEN_ACCESS);
    }

    @Test
    @DisplayName("createItem : 상품 등록 성공")
    void createItemFromOwner() {
        UUID shopId = UUID.randomUUID();

        Shop shop = mockShop(shopId);
        given(shopRepository.findById(shopId))
            .willReturn(Optional.of(shop));

        ItemRequest itemRequest = ItemRequest.builder()
            .shopId(String.valueOf(shopId))
            .name("김치찜")
            .price(15000)
            .description("맛있는 김치찜")
            .aiGenerated(false)
            .build();

        ItemResponse res = itemService.createItem(OWNER_ID, UserRole.OWNER, itemRequest);

        assertThat(res).isNotNull();
    }

    @Test
    @DisplayName("get item from owner : 가게 주인 불일치")
    void getItemFromAnotherOwner() {
        UUID shopId = UUID.randomUUID();

        Shop shop = mockShop(shopId);

        Item item = mock(Item.class);
        given(item.getShop()).willReturn(shop);

        UUID itemId = UUID.randomUUID();
        given(itemRepository.findByItemIdWithNativeQuery(itemId))
            .willReturn(Optional.of(item));

        assertThatThrownBy(() -> itemService.getItemFromOwner(ANOTHER_OWNER_ID, UserRole.OWNER, itemId))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FORBIDDEN_ACCESS);
    }

    @Test
    @DisplayName("get item from owner : 조회 성공")
    void getItemFromOwner() {
        UUID shopId = UUID.randomUUID();

        Shop shop = mockShop(shopId);

        Item item = mock(Item.class);
        given(item.getShop()).willReturn(shop);

        UUID itemId = UUID.randomUUID();
        given(itemRepository.findByItemIdWithNativeQuery(itemId))
            .willReturn(Optional.of(item));

        ItemOwnerDetailResponse res = itemService.getItemFromOwner(OWNER_ID, UserRole.OWNER, itemId);

        assertThat(res).isNotNull();
    }

    @Test
    @DisplayName("get item from customer : 조회 성공")
    void getItemFromCustomer() {
        UUID itemId = UUID.randomUUID();
        given(itemRepository.findByItemIdWithoutHidden(itemId)).willReturn(Optional.of(mock(Item.class)));

        ItemDetailResponse res = itemService.getItemFromCustomer(itemId);

        assertThat(res).isNotNull();
    }

    @Test
    @DisplayName("get items from owner : 가게 주인 불일치")
    void getItemsByShopFromAnotherOwner() {
        UUID shopId = UUID.randomUUID();

        Shop shop = mockShop(shopId);
        given(shopRepository.findById(shopId))
            .willReturn(Optional.of(shop));

        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

        assertThatThrownBy(() -> itemService.getItemsByShopFromOwner(ANOTHER_OWNER_ID, UserRole.OWNER, shopId, pageable))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FORBIDDEN_ACCESS);
    }

    @Test
    @DisplayName("get items from owner : 조회 성공")
    void getItemsByShopFromOwner() {
        UUID shopId = UUID.randomUUID();

        Shop shop = mockShop(shopId);
        given(shopRepository.findById(shopId))
            .willReturn(Optional.of(shop));

        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

        Item i1 = mock(Item.class);
        Item i2 = mock(Item.class);

        given(itemRepository.findAllByShopIdWithNativeQuery(shopId, pageable))
            .willReturn(new PageImpl<>(List.of(i1, i2)));

        Page<ItemResponse> res = itemService.getItemsByShopFromOwner(OWNER_ID, UserRole.OWNER, shopId, pageable);
        assertThat(res).isNotNull();
        assertThat(res.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("get items from customer : 조회 성공")
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
    @DisplayName("update items from owner : 가게 주인 불일치")
    void updateItemFromAnotherOwner() {
        UUID shopId = UUID.randomUUID();
        Shop shop = mockShop(shopId);

        UUID itemId = UUID.randomUUID();
        Item item = mock(Item.class);
        given(item.getShop()).willReturn(shop);

        given(itemRepository.findByItemIdWithNativeQuery(itemId))
            .willReturn(Optional.of(item));

        ItemUpdateRequest itemRequest = new ItemUpdateRequest(
            "김치찜",
            15000,
            "image",
            "맛있는 김치찜",
            false
        );

        assertThatThrownBy(() -> itemService.updateItem(ANOTHER_OWNER_ID, UserRole.OWNER, itemId, itemRequest))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FORBIDDEN_ACCESS);
    }

    @Test
    @DisplayName("update items from owner : 수정 성공")
    void updateItemFromOwner() {
        UUID shopId = UUID.randomUUID();
        Shop shop = mockShop(shopId);

        UUID itemId = UUID.randomUUID();
        Item item = mock(Item.class);
        given(item.getShop()).willReturn(shop);

        given(itemRepository.findByItemIdWithNativeQuery(itemId))
            .willReturn(Optional.of(item));

        ItemUpdateRequest itemRequest = new ItemUpdateRequest(
            "김치찜",
            15000,
            "image",
            "맛있는 김치찜",
            false
        );

        ItemResponse res = itemService.updateItem(OWNER_ID, UserRole.OWNER, itemId, itemRequest);

        assertThat(res).isNotNull();
    }

    @Test
    @DisplayName("soft delete : 가게 주인 불일치")
    void softDeleteFromAnotherOwner() {
        UUID shopId = UUID.randomUUID();
        Shop shop = mockShop(shopId);

        UUID itemId = UUID.randomUUID();
        Item item = mock(Item.class);
        given(item.getShop()).willReturn(shop);

        given(itemRepository.findByItemIdWithNativeQuery(itemId))
            .willReturn(Optional.of(item));

        assertThatThrownBy(() -> itemService.softDelete(ANOTHER_OWNER_ID, UserRole.OWNER, itemId))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FORBIDDEN_ACCESS);
    }

    @Test
    @DisplayName("soft delete : 삭제 성공")
    void softDeleteFromOwner() {
        UUID shopId = UUID.randomUUID();
        Shop shop = mockShop(shopId);

        UUID itemId = UUID.randomUUID();
        Item item = mock(Item.class);
        given(item.getShop()).willReturn(shop);

        given(itemRepository.findByItemIdWithNativeQuery(itemId))
            .willReturn(Optional.of(item));

        itemService.softDelete(OWNER_ID, UserRole.OWNER, itemId);

        verify(item).softDelete(OWNER_ID);
    }

    @Test
    @DisplayName("restore : 가게 주인 불일치")
    void restoreFromAnotherOwner() {
        UUID shopId = UUID.randomUUID();
        Shop shop = mockShop(shopId);

        UUID itemId = UUID.randomUUID();
        Item item = mock(Item.class);
        given(item.getShop()).willReturn(shop);

        given(itemRepository.findByItemIdWithNativeQuery(itemId))
            .willReturn(Optional.of(item));

        assertThatThrownBy(() -> itemService.restoreItem(ANOTHER_OWNER_ID, UserRole.OWNER, itemId))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FORBIDDEN_ACCESS);
    }

    @Test
    @DisplayName("restore : 복구 성공")
    void restoreFromOwner() {
        UUID shopId = UUID.randomUUID();
        Shop shop = mockShop(shopId);

        UUID itemId = UUID.randomUUID();
        Item item = mock(Item.class);
        given(item.getShop()).willReturn(shop);

        given(itemRepository.findByItemIdWithNativeQuery(itemId))
            .willReturn(Optional.of(item));

        itemService.restoreItem(OWNER_ID, UserRole.OWNER, itemId);

        verify(item).restore();
    }

    @Test
    @DisplayName("toggle hidden : 가게 주인 불일치")
    void toggleHiddenFromAnotherOwner() {
        UUID shopId = UUID.randomUUID();
        Shop shop = mockShop(shopId);

        UUID itemId = UUID.randomUUID();
        Item item = mock(Item.class);
        given(item.getShop()).willReturn(shop);

        given(itemRepository.findByItemIdWithNativeQuery(itemId))
            .willReturn(Optional.of(item));

        assertThatThrownBy(() -> itemService.toggleHidden(ANOTHER_OWNER_ID, UserRole.OWNER, itemId))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FORBIDDEN_ACCESS);
    }

    @Test
    @DisplayName("toggle hidden : 숨김/해제 성공")
    void toggleHiddenFromOwner() {
        UUID shopId = UUID.randomUUID();
        Shop shop = mockShop(shopId);

        UUID itemId = UUID.randomUUID();
        Item item = mock(Item.class);
        given(item.getShop()).willReturn(shop);

        given(itemRepository.findByItemIdWithNativeQuery(itemId))
            .willReturn(Optional.of(item));

        itemService.toggleHidden(OWNER_ID, UserRole.OWNER, itemId);

        verify(item).toggleIsHidden();
    }
}