package com.delivery.justonebite.item.application.service;

import com.delivery.justonebite.global.exception.custom.CustomException;
import com.delivery.justonebite.global.exception.response.ErrorCode;
import com.delivery.justonebite.ai_history.domain.entity.AiRequestHistory;
import com.delivery.justonebite.ai_history.domain.repository.AiRequestHistoryRepository;
import com.delivery.justonebite.item.domain.entity.Item;
import com.delivery.justonebite.item.domain.repository.ItemRepository;
import com.delivery.justonebite.item.infrastructure.api.gemini.client.GeminiClient;
import com.delivery.justonebite.item.presentation.dto.ItemDetailResponse;
import com.delivery.justonebite.item.presentation.dto.ItemResponse;
import com.delivery.justonebite.item.presentation.dto.ItemRequest;
import com.delivery.justonebite.item.presentation.dto.ItemUpdateRequest;
import com.delivery.justonebite.shop.domain.entity.Shop;
import com.delivery.justonebite.shop.domain.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    private final ShopRepository shopRepository;

    private final GeminiClient geminiClient;

    private final AiRequestHistoryRepository aiRequestHistoryRepository;

    @Transactional
    public ItemResponse createItem(ItemRequest request) {
        Shop shop = shopRepository.findById(UUID.fromString(request.shopId())).orElseThrow(
            () -> new CustomException(ErrorCode.INVALID_SHOP)
        );
        Item item = request.toItem();
        item.setShop(shop);
      
        if (request.aiGenerated()) { // 상품 소개 AI API를 통해 작성
            String response = generateAiResponse(item, request.description());

            // AI 사용 기록 저장
            saveAiRequestHistory(1L, request.description(), response);
        } else { // 상품 소개 직접 작성
            itemRepository.save(item);
        }

        return ItemResponse.from(item);
    }

    public ItemDetailResponse getItemFromOwner(UUID itemId) {
        return ItemDetailResponse.from(itemRepository.findByItemIdWithNativeQuery(itemId).orElseThrow(
            () -> new CustomException(ErrorCode.INVALID_ITEM)
        ));
    }

    public ItemDetailResponse getItemFromCustomer(UUID itemId) {
        return ItemDetailResponse.from(itemRepository.findByItemId(itemId).orElseThrow(
            () -> new CustomException(ErrorCode.INVALID_ITEM)
        ));
    }

    public Page<ItemResponse> getItemsByShop(UUID shopId, Pageable pageable) { // owner 입장에서의 상품 조회
        return itemRepository.findAllByShopIdWithNativeQuery(shopId, pageable).map(ItemResponse::from);
    }

    public Page<ItemResponse> getItemsByShopWithoutHidden(UUID shopId, Pageable pageable) { // customer 입장에서의 상품 조회
        return itemRepository.findAllByShopIdWithoutHidden(shopId, pageable).map(ItemResponse::from);
    }

    @Transactional
    public ItemResponse updateItem(UUID itemId, ItemUpdateRequest request) {
        Item item = itemRepository.findByItemId(itemId).orElseThrow(
            () -> new CustomException(ErrorCode.INVALID_ITEM)
        );

        item.updateItem(request);

        if (request.aiGenerated()) {
            String response = generateAiResponse(item, request.description());

            // AI 사용 기록 저장
            saveAiRequestHistory(1L, request.description(), response);
        } else {
            itemRepository.save(item);
        }

        return ItemResponse.from(item);
    }

    @Transactional
    public void softDelete(Long deleterId, UUID itemId) {
        Item item = itemRepository.findByItemId(itemId).orElseThrow(() -> new CustomException(ErrorCode.INVALID_ITEM));
        item.softDelete(deleterId);
        itemRepository.save(item);
    }

    @Transactional
    public void restoreItem(UUID itemId) {
        Item item = itemRepository.findByItemIdWithNativeQuery(itemId).orElseThrow(
            () -> new CustomException(ErrorCode.INVALID_ITEM)
        );
        item.restore();
        itemRepository.save(item);
    }

    @Transactional
    public void toggleHidden(UUID itemId) {
        Item item = itemRepository.findByItemId(itemId).orElseThrow(
            () -> new CustomException(ErrorCode.INVALID_ITEM)
        );
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
}
