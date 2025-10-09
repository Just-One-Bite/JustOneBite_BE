package com.delivery.justonebite.item.presentation.controller;

import com.delivery.justonebite.item.application.service.ItemService;
import com.delivery.justonebite.item.presentation.dto.ItemDetailResponse;
import com.delivery.justonebite.item.presentation.dto.ItemResponse;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/items")
public class ItemController {

    private final ItemService itemService;

    // require fix : 추후 role과 같은 이슈 해결 필요 -> @AuthenticationPrincipal UserDetails userDetails
    @PostMapping
    public ResponseEntity<ItemResponse> createItem(@RequestBody @Valid ItemRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(itemService.createItem(request));
    }

    @GetMapping("/owner/{item-id}")
    public ResponseEntity<ItemDetailResponse> getItemFromOwner(@PathVariable("item-id") UUID itemId) {
        return ResponseEntity.status(HttpStatus.OK).body(itemService.getItemFromOwner(itemId));
    }

    @GetMapping("/{item-id}")
    public ResponseEntity<ItemDetailResponse> getItemFromCustomer(@PathVariable("item-id") UUID itemId) {
        return ResponseEntity.status(HttpStatus.OK).body(itemService.getItemFromCustomer(itemId));
    }

    @GetMapping("/shop/owner/{shop-id}") // owner 용, fix : 추후 권한에 따라서 다른 service를 쓰도록 할 것 같음
    public ResponseEntity<Page<ItemResponse>> getItemsByShopFromOwner(@PathVariable("shop-id") String shopId,
                                                             @RequestParam(name = "page", defaultValue = "0") int page,
                                                             @RequestParam(name = "size", defaultValue = "10") int size,
                                                             @RequestParam(name = "sort-by", defaultValue = "createdAt") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(changeSnakeCase(sortBy)));
        return ResponseEntity.status(HttpStatus.OK).body(itemService.getItemsByShop(UUID.fromString(shopId), pageable));
    }

    @GetMapping("/shop/{shop-id}") // customer 용
    public ResponseEntity<Page<ItemResponse>> getItemsByShopFromCustomer(@PathVariable("shop-id") String shopId,
                                                                          @RequestParam(name = "page", defaultValue = "0") int page,
                                                                          @RequestParam(name = "size", defaultValue = "10") int size,
                                                                          @RequestParam(name = "sort-by", defaultValue = "createdAt") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return ResponseEntity.status(HttpStatus.OK).body(itemService.getItemsByShopWithoutHidden(UUID.fromString(shopId), pageable));
    }

    @PutMapping("/{item-id}")
    public ResponseEntity<ItemResponse> updateItem(@PathVariable("item-id") UUID itemId, @RequestBody @Valid ItemUpdateRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(itemService.updateItem(itemId, request));
    }

    @DeleteMapping("/{item-id}")
    public ResponseEntity<Void> softDelete(@PathVariable("item-id") String itemId) {
        itemService.softDelete(1L, UUID.fromString(itemId));
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/{item-id}/restore")
    public ResponseEntity<Void> restoreItem(@PathVariable("item-id") String itemId) {
        itemService.restoreItem(UUID.fromString(itemId));
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/{item-id}/hide")
    public ResponseEntity<Void> toggleHidden(@PathVariable("item-id") String itemId) {
        itemService.toggleHidden(UUID.fromString(itemId));
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    private String changeSnakeCase(String sortBy) {
        Pattern pattern = Pattern.compile("([a-z])([A-Z])");
        Matcher matcher = pattern.matcher(sortBy);

        return matcher.replaceAll(matchResult ->
            String.format("%s_%s", matchResult.group(1), matchResult.group(2))
        );
    }
}
