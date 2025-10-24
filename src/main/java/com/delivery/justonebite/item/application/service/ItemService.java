package com.delivery.justonebite.item.application.service;

import com.delivery.justonebite.ai_history.domain.entity.AiRequestHistory;
import com.delivery.justonebite.ai_history.domain.repository.AiRequestHistoryRepository;
import com.delivery.justonebite.global.exception.custom.CustomException;
import com.delivery.justonebite.global.exception.response.ErrorCode;
import com.delivery.justonebite.item.domain.entity.Item;
import com.delivery.justonebite.item.domain.repository.ItemRepository;
import com.delivery.justonebite.item.infrastructure.api.gemini.service.GeminiService;
import com.delivery.justonebite.item.application.dto.request.ItemRequest;
import com.delivery.justonebite.item.application.dto.request.ItemUpdateRequest;
import com.delivery.justonebite.item.application.dto.response.ItemDetailResponse;
import com.delivery.justonebite.item.application.dto.response.ItemOwnerDetailResponse;
import com.delivery.justonebite.item.application.dto.response.ItemResponse;
import com.delivery.justonebite.shop.domain.entity.Shop;
import com.delivery.justonebite.shop.domain.repository.ShopRepository;
import com.delivery.justonebite.user.domain.entity.User;
import com.delivery.justonebite.user.domain.entity.UserRole;
import com.delivery.justonebite.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private final ShopRepository shopRepository;

    private final GeminiService geminiService;

    private final AiRequestHistoryRepository aiRequestHistoryRepository;

    @Transactional
    public ItemResponse createItem(Long userId, UserRole role, ItemRequest request) {
        Shop shop = checkValidRequestWithShop(userId, role, UUID.fromString(request.shopId()));

        Item item = request.toItem();
        item.updateShop(shop);
      
        if (request.aiGenerated()) {
            String response = generateAiResponse(item, request.description());
            saveAiRequestHistory(userId, request.description(), response);
        } else {
            itemRepository.save(item);
        }

        return ItemResponse.from(item);
    }

    public ItemOwnerDetailResponse getItemFromOwner(Long userId, UserRole role, UUID itemId) {
        Item item = checkValidRequestWithItem(userId, role, itemId);

        return ItemOwnerDetailResponse.from(item);
    }

    public ItemDetailResponse getItemFromCustomer(UUID itemId) {
        return ItemDetailResponse.from(itemRepository.findByItemIdWithoutHidden(itemId).orElseThrow(
            () -> new CustomException(ErrorCode.INVALID_ITEM)
        ));
    }

    public Page<ItemResponse> getItemsByShopFromOwner(Long userId, UserRole role, UUID shopId, Pageable pageable) {
        checkValidRequestWithShop(userId, role, shopId);

        return itemRepository.findAllByShopIdWithNativeQuery(shopId, pageable).map(ItemResponse::from);
    }

    public Page<ItemResponse> getItemsByShopFromCustomer(UUID shopId, Pageable pageable) { // customer 입장에서의 상품 조회
        return itemRepository.findAllByShopIdWithoutHidden(shopId, pageable).map(ItemResponse::from);
    }

    @Transactional
    public ItemResponse updateItem(Long userId, UserRole role, UUID itemId, ItemUpdateRequest request) {
        Item item = checkValidRequestWithItem(userId, role, itemId);

        item.updateItem(request);

        if (request.aiGenerated()) {
            String response = generateAiResponse(item, request.description());
            saveAiRequestHistory(userId, request.description(), response);
        }

        return ItemResponse.from(item);
    }

    @Transactional
    public void softDelete(Long userId, UserRole role, UUID itemId) {
        Item item = checkValidRequestWithItem(userId, role, itemId);

        item.softDelete(userId);
    }

    @Transactional
    public void restoreItem(Long userId, UserRole role, UUID itemId) {
        Item item = checkValidRequestWithItem(userId, role, itemId);

        item.restore();
    }

    @Transactional
    public void toggleHidden(Long userId, UserRole role, UUID itemId) {
        Item item = checkValidRequestWithItem(userId, role, itemId);

        item.toggleIsHidden();
    }

    private String generateAiResponse(Item item, String prompt) {
        String response = geminiService.generateAiResponse(prompt);
        item.updateDescription(response);
        return response;
    }

    private void saveAiRequestHistory(Long userId, String request, String response) {
        User user = userRepository.findById(userId).orElseThrow(
            () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        AiRequestHistory requestHistory = new AiRequestHistory(user, "gemini-2.5-flash", request, response);
        aiRequestHistoryRepository.save(requestHistory);
    }

    private Item checkValidRequestWithItem(Long userId, UserRole role, UUID itemId) {
        Item item = itemRepository.findByItemIdWithNativeQuery(itemId).orElseThrow(
            () -> new CustomException(ErrorCode.INVALID_ITEM)
        );

        isOwner(item.getShop(), userId, role);

        return item;
    }

    private Shop checkValidRequestWithShop(Long userId, UserRole role, UUID shopId) {
        Shop shop = shopRepository.findById(shopId).orElseThrow(
            () -> new CustomException(ErrorCode.SHOP_NOT_FOUND)
        );

        isOwner(shop, userId, role);

        return shop;
    }

    private void isOwner(Shop shop, Long userId, UserRole role) {
        if (role.equals(UserRole.MANAGER) || role.equals(UserRole.MASTER) || shop.getOwnerId().equals(userId)) {
            return;
        }
        throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
    }
}
