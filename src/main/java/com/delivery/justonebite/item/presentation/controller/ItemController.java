package com.delivery.justonebite.item.presentation.controller;

import com.delivery.justonebite.global.common.security.UserDetailsImpl;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/items")
public class ItemController {

    private final ItemService itemService;

    // 상품 CREATE
    @PostMapping
    public ResponseEntity<ItemResponse> createItem(@RequestBody @Valid ItemRequest request,
                                                   @AuthenticationPrincipal UserDetailsImpl user) {
        return ResponseEntity.status(HttpStatus.OK).body(
            itemService.createItem(user.getUserId(), user.getUserRole(), request)
        );
    }

    // 숨김 및 삭제 상품 포함 단건 Read
    @GetMapping("/owner/{item-id}")
    public ResponseEntity<ItemDetailResponse> getItemFromOwner(@PathVariable("item-id") UUID itemId,
                                                               @AuthenticationPrincipal UserDetailsImpl user) {
        return ResponseEntity.status(HttpStatus.OK).body(
            itemService.getItemFromOwner(user.getUserId(), user.getUserRole(), itemId)
        );
    }

    // 숨김 및 삭제 상품 제외 단건 Read
    @GetMapping("/{item-id}")
    public ResponseEntity<ItemDetailResponse> getItemFromCustomer(@PathVariable("item-id") UUID itemId) {
        return ResponseEntity.status(HttpStatus.OK).body(
            itemService.getItemFromCustomer(itemId)
        );
    }

    // 가게별 숨김 및 삭제 상품 포함 Read
    @GetMapping("/owner/shop/{shop-id}") // owner 용, fix : 추후 권한에 따라서 다른 service를 쓰도록 할 것 같음
    public ResponseEntity<Page<ItemResponse>> getItemsByShopFromOwner(@PathVariable("shop-id") String shopId,
                                                                      @RequestParam(name = "page", defaultValue = "0") int page,
                                                                      @RequestParam(name = "size", defaultValue = "10") int size,
                                                                      @RequestParam(name = "sort-by", defaultValue = "createdAt") String sortBy,
                                                                      @AuthenticationPrincipal UserDetailsImpl user) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(changeSnakeCase(sortBy)));
        return ResponseEntity.status(HttpStatus.OK).body(
            itemService.getItemsByShopFromOwner(user.getUserId(), user.getUserRole(), UUID.fromString(shopId), pageable)
        );
    }

    // 가게별 숨김 및 삭제 상품 제외 Read
    @GetMapping("/shop/{shop-id}") // customer 용
    public ResponseEntity<Page<ItemResponse>> getItemsByShopFromCustomer(@PathVariable("shop-id") String shopId,
                                                                          @RequestParam(name = "page", defaultValue = "0") int page,
                                                                          @RequestParam(name = "size", defaultValue = "10") int size,
                                                                          @RequestParam(name = "sort-by", defaultValue = "createdAt") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return ResponseEntity.status(HttpStatus.OK).body(
            itemService.getItemsByShopFromCustomer(UUID.fromString(shopId), pageable)
        );
    }

    // 상품 Update
    @PutMapping("/{item-id}")
    public ResponseEntity<ItemResponse> updateItem(@PathVariable("item-id") UUID itemId, @RequestBody @Valid ItemUpdateRequest request,
                                                   @AuthenticationPrincipal UserDetailsImpl user) {
        return ResponseEntity.status(HttpStatus.OK).body(
            itemService.updateItem(user.getUserId(), user.getUserRole(), itemId, request)
        );
    }

    // 상품 Soft Delete
    @DeleteMapping("/{item-id}")
    public ResponseEntity<Void> softDelete(@PathVariable("item-id") String itemId,
                                           @AuthenticationPrincipal UserDetailsImpl user) {
        itemService.softDelete(user.getUserId(), user.getUserRole(), UUID.fromString(itemId));
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 상품 Restore
    @PatchMapping("/{item-id}/restore")
    public ResponseEntity<Void> restoreItem(@PathVariable("item-id") String itemId,
                                            @AuthenticationPrincipal UserDetailsImpl user) {
        itemService.restoreItem(user.getUserId(), user.getUserRole(), UUID.fromString(itemId));
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 상품 Hidden / Reveal
    @PatchMapping("/{item-id}/hide")
    public ResponseEntity<Void> toggleHidden(@PathVariable("item-id") String itemId,
                                             @AuthenticationPrincipal UserDetailsImpl user) {
        itemService.toggleHidden(user.getUserId(), user.getUserRole(), UUID.fromString(itemId));
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
