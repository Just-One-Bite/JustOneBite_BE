package com.delivery.justonebite.item.application.service;

import com.delivery.justonebite.global.exception.custom.CustomException;
import com.delivery.justonebite.global.exception.response.ErrorCode;
import com.delivery.justonebite.ai_history.domain.entity.AiRequestHistory;
import com.delivery.justonebite.ai_history.domain.repository.AiRequestHistoryRepository;
import com.delivery.justonebite.item.domain.entity.Item;
import com.delivery.justonebite.item.domain.repository.ItemRepository;
import com.delivery.justonebite.item.infrastructure.api.gemini.client.GeminiClient;
import com.delivery.justonebite.item.presentation.dto.*;
import com.delivery.justonebite.shop.domain.entity.Shop;
import com.delivery.justonebite.shop.domain.repository.ShopRepository;
import com.delivery.justonebite.user.domain.entity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    private final ShopRepository shopRepository;

    private final GeminiClient geminiClient;

    private final AiRequestHistoryRepository aiRequestHistoryRepository;

    // 상품 CREATE
    @Transactional
    public ItemResponse createItem(Long userId, UserRole role, ItemRequest request) {
        Shop shop = checkValidRequestWithShop(userId, role, UUID.fromString(request.shopId()));

        Item item = request.toItem();
        item.setShop(shop);
      
        if (request.aiGenerated()) {
            String response = generateAiResponse(item, request.description());
            saveAiRequestHistory(userId, request.description(), response);
        } else {
            itemRepository.save(item);
        }

        return ItemResponse.from(item);
    }

    // 숨김 및 삭제 상품 포함 단건 Read
    public ItemOwnerDetailResponse getItemFromOwner(Long userId, UserRole role, UUID itemId) {
        Item item = checkValidRequestWithItem(userId, role, itemId);

        return ItemOwnerDetailResponse.from(item);
    }

    // 숨김 및 삭제 상품 제외 단건 Read
    public ItemDetailResponse getItemFromCustomer(UUID itemId) {
        return ItemDetailResponse.from(itemRepository.findByItemIdWithoutHidden(itemId).orElseThrow(
            () -> new CustomException(ErrorCode.INVALID_ITEM)
        ));
    }

    // 가게별 숨김 및 삭제 상품 포함 Read
    public Page<ItemResponse> getItemsByShopFromOwner(Long userId, UserRole role, UUID shopId, Pageable pageable) {
        checkValidRequestWithShop(userId, role, shopId);

        return itemRepository.findAllByShopIdWithNativeQuery(shopId, pageable).map(ItemResponse::from);
    }

    // 가게별 숨김 및 삭제 상품 제외 Read
    public Page<ItemResponse> getItemsByShopFromCustomer(UUID shopId, Pageable pageable) { // customer 입장에서의 상품 조회
        return itemRepository.findAllByShopIdWithoutHidden(shopId, pageable).map(ItemResponse::from);
    }

    // 상품 Update
    @Transactional
    public ItemResponse updateItem(Long userId, UserRole role, UUID itemId, ItemUpdateRequest request) {
        Item item = checkValidRequestWithItem(userId, role, itemId);

        item.updateItem(request);

        if (request.aiGenerated()) {
            String response = generateAiResponse(item, request.description());
            saveAiRequestHistory(userId, request.description(), response);
        } else {
            itemRepository.save(item);
        }

        return ItemResponse.from(item);
    }

    // 상품 Soft Delete
    @Transactional
    public void softDelete(Long userId, UserRole role, UUID itemId) {
        Item item = checkValidRequestWithItem(userId, role, itemId);

        item.softDelete(userId);
        itemRepository.save(item);
    }

    // 상품 Restore
    @Transactional
    public void restoreItem(Long userId, UserRole role, UUID itemId) {
        Item item = checkValidRequestWithItem(userId, role, itemId);

        item.restore();
        itemRepository.save(item);
    }

    // 상품 Hidden / Reveal
    @Transactional
    public void toggleHidden(Long userId, UserRole role, UUID itemId) {
        Item item = checkValidRequestWithItem(userId, role, itemId);

        item.toggleIsHidden();
        itemRepository.save(item);
    }

    private String generateAiResponse(Item item, String prompt) {
        String response = geminiClient.generateAiResponse(prompt);
        item.updateDescription(response);
        itemRepository.save(item);
        return response;
    }

    private void saveAiRequestHistory(Long userId, String request, String response) {
        AiRequestHistory requestHistory = new AiRequestHistory(userId, "gemini-2.5-flash", request, response);
        aiRequestHistoryRepository.save(requestHistory);
    }

    // Read, Update, Delete, Hide : 상품의 제어 권한 보유 여부 확인
    private Item checkValidRequestWithItem(Long userId, UserRole role, UUID itemId) {
        authorization(Set.of(UserRole.OWNER, UserRole.MANAGER, UserRole.MASTER), role);

        Item item = itemRepository.findByItemIdWithNativeQuery(itemId).orElseThrow(
            () -> new CustomException(ErrorCode.INVALID_ITEM)
        );

        isOwner(item.getShop(), userId, role);

        return item;
    }

    // Create, ReadAll : 상품의 제어 권한 보유 여부 확인
    private Shop checkValidRequestWithShop(Long userId, UserRole role, UUID shopId) {
        authorization(Set.of(UserRole.OWNER, UserRole.MANAGER, UserRole.MASTER), role);

        Shop shop = shopRepository.findById(shopId).orElseThrow(
            () -> new CustomException(ErrorCode.INVALID_SHOP)
        );

        isOwner(shop, userId, role);

        return shop;
    }

    private void authorization(Set<UserRole> validRoles, UserRole role) {
        if (validRoles.contains(role)) {
            return;
        }
        throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
    }

    private void isOwner(Shop shop, Long userId, UserRole role) {
        if (role.equals(UserRole.MANAGER) || role.equals(UserRole.MASTER) || shop.getOwner().equals(userId)) {
            return;
        }
        throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
    }
}
