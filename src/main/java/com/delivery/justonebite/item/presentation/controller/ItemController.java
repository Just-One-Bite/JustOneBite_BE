package com.delivery.justonebite.item.presentation.controller;

import com.delivery.justonebite.item.application.service.ItemService;
import com.delivery.justonebite.item.presentation.dto.ItemDetailResponse;
import com.delivery.justonebite.item.presentation.dto.ItemReponse;
import com.delivery.justonebite.item.presentation.dto.ItemRequest;
import com.delivery.justonebite.item.presentation.dto.ItemUpdateRequest;
import jakarta.validation.Valid;
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
    @PostMapping
    public ResponseEntity<Void> createItem(@RequestBody @Valid ItemRequest request) {
        itemService.createItem(request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/{item-id}")
    public ResponseEntity<ItemDetailResponse> getItem(@PathVariable("item-id") UUID itemId) {
        return ResponseEntity.status(HttpStatus.OK).body(itemService.getItem(itemId));
    }

    @GetMapping("/shop/{shop-id}")
    public ResponseEntity<Page<ItemReponse>> getItemsByShop(@PathVariable("shop-id") String shopId,
                                                            @RequestParam(name = "page", defaultValue = "0") int page,
                                                            @RequestParam(name = "size", defaultValue = "10") int size,
                                                            @RequestParam(name = "sort-by", defaultValue = "createdAt") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return ResponseEntity.status(HttpStatus.OK).body(itemService.getItemsByShop(UUID.fromString(shopId), pageable));
    }

    @PutMapping("/{item-id}")
    public ResponseEntity<Void> updateItem(@PathVariable("item-id") String itemId, @RequestBody @Valid ItemUpdateRequest request) {
        itemService.updateItem(UUID.fromString(itemId), request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{item-id}")
    public ResponseEntity<Void> deleteItem(@PathVariable("item-id") String itemId) {
        itemService.deleteItem(UUID.fromString(itemId));
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/{item-id}/hide")
    public ResponseEntity<Void> toggleHidden(@PathVariable("item-id") String itemId) {
        itemService.toggleHidden(UUID.fromString(itemId));
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
