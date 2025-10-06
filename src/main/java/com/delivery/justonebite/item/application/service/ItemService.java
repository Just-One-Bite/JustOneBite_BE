package com.delivery.justonebite.item.application.service;

import com.delivery.justonebite.global.exception.custom.CustomException;
import com.delivery.justonebite.global.exception.response.ErrorCode;
import com.delivery.justonebite.item.domain.entity.Item;
import com.delivery.justonebite.item.domain.repository.ItemRepository;
import com.delivery.justonebite.item.presentation.dto.ItemDetailResponse;
import com.delivery.justonebite.item.presentation.dto.ItemReponse;
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

    @Transactional
    public ItemReponse createItem(ItemRequest request) {
        Shop shop = shopRepository.findById(UUID.fromString(request.shopId())).orElseThrow(() -> new CustomException(ErrorCode.INVALID_SHOP));
        Item item = request.toItem();
        item.setShop(shop);

        itemRepository.save(item);

        return ItemReponse.from(item);
    }

    public ItemDetailResponse getItem(UUID itemId) {
        return ItemDetailResponse.from(itemRepository.findByItemId(itemId).orElseThrow(() -> new CustomException(ErrorCode.INVALID_ITEM)));
    }

    public Page<ItemReponse> getItemsByShop(UUID shopId, Pageable pageable) {
        return itemRepository.findAllByShopId(shopId, pageable).map(ItemReponse::from);
    }

    @Transactional
    public ItemReponse updateItem(UUID itemId, ItemUpdateRequest request) {
        Item item = itemRepository.findByItemId(itemId).orElseThrow(() -> new CustomException(ErrorCode.INVALID_ITEM));
        item.updateItem(request);
        itemRepository.save(item);
        return ItemReponse.from(item);
    }

    @Transactional
    public void deleteItem(UUID itemId) {
        Item item = itemRepository.findByItemId(itemId).orElseThrow(() -> new CustomException(ErrorCode.INVALID_ITEM));
        itemRepository.delete(item);
    }

    @Transactional
    public void toggleHidden(UUID itemId) {
        Item item = itemRepository.findByItemId(itemId).orElseThrow(() -> new CustomException(ErrorCode.INVALID_ITEM));
        item.toggleIsHidden();
        itemRepository.save(item);
    }
}
