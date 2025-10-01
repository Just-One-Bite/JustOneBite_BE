package com.delivery.justonebite.item.presentation.controller;

import com.delivery.justonebite.item.application.service.ItemService;
import com.delivery.justonebite.item.presentation.dto.ItemDetailResponseDto;
import com.delivery.justonebite.item.presentation.dto.ItemReponseDto;
import com.delivery.justonebite.item.presentation.dto.ItemRequestDto;
import com.delivery.justonebite.item.presentation.dto.ItemUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/items")
public class ItemController {

    private final ItemService itemService;

    // require fix : 추후 role과 같은 이슈 해결 필요 -> @AuthenticationPrincipal UserDetails userDetails
    @PostMapping("/")
    public ResponseEntity<Void> createItem(@RequestBody ItemRequestDto requestDto) {
        itemService.createItem(requestDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/{item-id}")
    public ResponseEntity<ItemDetailResponseDto> getItem(@PathVariable("item-id") UUID itemId) {
        return ResponseEntity.status(HttpStatus.OK).body(itemService.getItem(itemId));
    }

    @GetMapping("/shop/{shop-id}")
    public ResponseEntity<Page<ItemReponseDto>> getItemsByShop(@PathVariable("shop-id") UUID shopId,
                                                               @RequestParam(name = "page", defaultValue = "0") int page,
                                                               @RequestParam(name = "size", defaultValue = "10") int size,
                                                               @RequestParam(name = "sort-by", defaultValue = "createdAt") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return ResponseEntity.status(HttpStatus.OK).body(itemService.getItemsByShop(shopId, pageable));
    }

    @PutMapping("/{item-id}")
    public ResponseEntity<Void> updateItem(@PathVariable("item-id") UUID itemId, @RequestBody ItemUpdateRequestDto requestDto) {
        itemService.updateItem(itemId, requestDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{item-id}")
    public ResponseEntity<Void> deleteItem(@PathVariable("item-id") UUID itemId) {
        itemService.deleteItem(itemId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/{item-id}/hide")
    public ResponseEntity<Void> toggleHidden(@PathVariable("item-id") UUID itemId) {
        itemService.toggleHidden(itemId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
