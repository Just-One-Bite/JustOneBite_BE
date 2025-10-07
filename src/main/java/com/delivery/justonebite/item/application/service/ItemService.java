package com.delivery.justonebite.item.application.service;

import com.delivery.justonebite.ai_history.domain.entity.AiRequestHistory;
import com.delivery.justonebite.ai_history.domain.repository.AiRequestHistoryRepository;
import com.delivery.justonebite.item.domain.entity.Item;
import com.delivery.justonebite.item.domain.repository.ItemRepository;
import com.delivery.justonebite.item.infrastructure.api.gemini.client.GeminiClient;
import com.delivery.justonebite.item.presentation.dto.ItemDetailResponse;
import com.delivery.justonebite.item.presentation.dto.ItemReponse;
import com.delivery.justonebite.item.presentation.dto.ItemRequest;
import com.delivery.justonebite.item.presentation.dto.ItemUpdateRequest;
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

    private final GeminiClient geminiClient;

    private final AiRequestHistoryRepository aiRequestHistoryRepository;

    @Transactional
    public void createItem(ItemRequest request) {
        Item item = request.toItem();
        if (item.isAiGenerated()) { // 상품 소개 AI API를 통해 작성
            String prompt = request.description();
            String response = generateAiResponse(item, prompt);

            // AI 사용 기록 저장
            saveAiRequestHistory(1L, prompt, response);
        } else { // 상품 소개 직접 작성
            itemRepository.save(item);
        }
    }

    public ItemDetailResponse getItem(UUID itemId) {
        return ItemDetailResponse.from(itemRepository.findByItemId(itemId).orElseThrow(IllegalArgumentException::new));
    }

    public Page<ItemReponse> getItemsByShop(UUID shopId, Pageable pageable) {
        return itemRepository.findAllByShopId(shopId, pageable).map(ItemReponse::from);
    }

    @Transactional
    public void updateItem(UUID itemId, ItemUpdateRequest request) {
        Item item = itemRepository.findByItemId(itemId).orElseThrow(IllegalArgumentException::new);
        item.updateItem(request);

        if (request.aiGenerated()) {
            String prompt = request.description();
            String response = generateAiResponse(item, prompt);

            // AI 사용 기록 저장
            saveAiRequestHistory(1L, prompt, response);
        } else {
            itemRepository.save(item);
        }
    }

    @Transactional
    public void deleteItem(UUID itemId) {
        Item item = itemRepository.findByItemId(itemId).orElseThrow(IllegalArgumentException::new);
        itemRepository.delete(item);
    }

    @Transactional
    public void toggleHidden(UUID itemId) {
        Item item = itemRepository.findByItemId(itemId).orElseThrow(IllegalArgumentException::new);
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
