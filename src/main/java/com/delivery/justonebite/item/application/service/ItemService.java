package com.delivery.justonebite.item.application.service;

import com.delivery.justonebite.item.domain.entity.ItemEntity;
import com.delivery.justonebite.item.domain.repository.ItemRepository;
import com.delivery.justonebite.item.presentation.dto.ItemDetailResponseDto;
import com.delivery.justonebite.item.presentation.dto.ItemReponseDto;
import com.delivery.justonebite.item.presentation.dto.ItemRequestDto;
import com.delivery.justonebite.item.presentation.dto.ItemUpdateRequestDto;
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

    public void createItem(ItemRequestDto requestDto) {
        itemRepository.save(requestDto.toEntity());
    }

    public ItemDetailResponseDto getItem(UUID itemId) {
        return itemRepository.findByItemId(itemId).orElseThrow(IllegalArgumentException::new).toItemDetailResponseDto();
    }

    public Page<ItemReponseDto> getItemsByShop(UUID shopId, Pageable pageable) {
        return itemRepository.findAllByShopId(shopId, pageable).map(ItemEntity::toItemReponseDto);
    }

    @Transactional
    public void updateItem(UUID itemId, ItemUpdateRequestDto requestDto) {
        ItemEntity item = itemRepository.findByItemId(itemId).orElseThrow(IllegalArgumentException::new);
        item.updateItem(requestDto);
        itemRepository.save(item);
    }

    @Transactional
    public void deleteItem(UUID itemId) {
        ItemEntity item = itemRepository.findByItemId(itemId).orElseThrow(IllegalArgumentException::new);
        itemRepository.delete(item);
    }

    @Transactional
    public void toggleHidden(UUID itemId) {
        ItemEntity item = itemRepository.findByItemId(itemId).orElseThrow(IllegalArgumentException::new);
        item.toggleIsHidden();
        itemRepository.save(item);
    }
}
