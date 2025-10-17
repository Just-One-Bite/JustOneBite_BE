package com.delivery.justonebite.item.presentation.controller;

import com.delivery.justonebite.global.config.security.UserDetailsImpl;
import com.delivery.justonebite.item.application.service.ItemService;
import com.delivery.justonebite.item.presentation.dto.request.ItemRequest;
import com.delivery.justonebite.item.presentation.dto.request.ItemUpdateRequest;
import com.delivery.justonebite.item.presentation.dto.response.ItemDetailResponse;
import com.delivery.justonebite.item.presentation.dto.response.ItemOwnerDetailResponse;
import com.delivery.justonebite.item.presentation.dto.response.ItemResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Tag(name = "Item API", description = "상품 생성/조회/수정/삭제/숨김 등을 담당합니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/items")
public class ItemController {
    private final ItemService itemService;

    @Operation(
        summary = "상품 등록 요청 API",
        description = "사용자(OWNER, MANAGER, MASTER)가 상품을 등록합니다. 해당 API 요청 권한은 OWNER, MANAGER, MASTER만 가능합니다.",
        security = @SecurityRequirement(name = "Authorization"),
        responses = {
            @ApiResponse(responseCode = "201", description = "상품 등록에 성공하였습니다."),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 요청입니다. (JWT 토큰 누락 또는 만료)", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "접근 권한이 없습니다. (OWNER, MANAGER, MASTER 아님)", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "가게 정보가 존재하지 않습니다.", content = @Content(mediaType = "application/json"))
        }
    )
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
    @PostMapping
    public ResponseEntity<ItemResponse> createItem(@RequestBody @Valid ItemRequest request,
                                                   @AuthenticationPrincipal UserDetailsImpl user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            itemService.createItem(user.getUserId(), user.getUserRole(), request)
        );
    }

    @Operation(
        summary = "모든 상태의 상품 단건 조회 요청 API",
        description = "사용자(OWNER, MANAGER, MASTER)가 상품을 조회합니다. 해당 API 요청 권한은 OWNER, MANAGER, MASTER만 가능합니다.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = {
            @Parameter(name = "item-id", description = "조회할 상품의 id value")
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "상품 조회에 성공하였습니다."),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 요청입니다. (JWT 토큰 누락 또는 만료)", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "접근 권한이 없습니다. (OWNER, MANAGER, MASTER 아님)", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "상품 정보가 존재하지 않습니다.", content = @Content(mediaType = "application/json"))
        }
    )
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
    @GetMapping("/owner/{item-id}")
    public ResponseEntity<ItemOwnerDetailResponse> getItemFromOwner(@PathVariable("item-id") UUID itemId,
                                                                    @AuthenticationPrincipal UserDetailsImpl user) {
        return ResponseEntity.status(HttpStatus.OK).body(
            itemService.getItemFromOwner(user.getUserId(), user.getUserRole(), itemId)
        );
    }

    @Operation(
        summary = "상품 단건 조회 요청 API",
        description = "사용자가 상품을 조회합니다.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = {
            @Parameter(name = "item-id", description = "조회할 상품의 id value")
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "상품 조회에 성공하였습니다."),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 요청입니다. (JWT 토큰 누락 또는 만료)", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "상품 정보가 존재하지 않습니다.", content = @Content(mediaType = "application/json"))
        }
    )
    @GetMapping("/{item-id}")
    public ResponseEntity<ItemDetailResponse> getItemFromCustomer(@PathVariable("item-id") UUID itemId) {
        return ResponseEntity.status(HttpStatus.OK).body(
            itemService.getItemFromCustomer(itemId)
        );
    }

    @Operation(
        summary = "모든 상태의 상품 목록 조회 요청 API",
        description = "사용자(OWNER, MANAGER, MASTER)가 상품을 조회합니다. 해당 API 요청 권한은 OWNER, MANAGER, MASTER만 가능합니다.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = {
            @Parameter(name = "shop-id", description = "조회할 가게의 id value"),
            @Parameter(name = "page", description = "조회할 목록의 페이지 번호", required = true),
            @Parameter(name = "size", description = "페이지 당 조회 개수", required = true),
            @Parameter(name = "sort-by", description = "상품 생성 시점 기준 정렬")
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "상품 목록 조회에 성공하였습니다."),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 요청입니다. (JWT 토큰 누락 또는 만료)", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "접근 권한이 없습니다. (OWNER, MANAGER, MASTER 아님)", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "가게 정보가 존재하지 않습니다.", content = @Content(mediaType = "application/json"))
        }
    )
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
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

    @Operation(
        summary = "상품 목록 조회 요청 API",
        description = "사용자가 상품을 조회합니다.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = {
            @Parameter(name = "shop-id", description = "조회할 가게의 id value"),
            @Parameter(name = "page", description = "조회할 목록의 페이지 번호", required = true),
            @Parameter(name = "size", description = "페이지 당 조회 개수", required = true),
            @Parameter(name = "sort-by", description = "상품 생성 시점 기준 정렬")
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "상품 목록 조회에 성공하였습니다."),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 요청입니다. (JWT 토큰 누락 또는 만료)", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "가게 정보가 존재하지 않습니다.", content = @Content(mediaType = "application/json"))
        }
    )
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

    @Operation(
        summary = "상품 수정 요청 API",
        description = "사용자(OWNER, MANAGER, MASTER)가 상품을 수정합니다. 해당 API 요청 권한은 OWNER, MANAGER, MASTER만 가능합니다.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = {
            @Parameter(name = "item-id", description = "수정할 상품의 id value")
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "상품 수정에 성공하였습니다."),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 요청입니다. (JWT 토큰 누락 또는 만료)", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "접근 권한이 없습니다. (OWNER, MANAGER, MASTER 아님)", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "상품 정보가 존재하지 않습니다.", content = @Content(mediaType = "application/json"))
        }
    )
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
    @PutMapping("/{item-id}")
    public ResponseEntity<ItemResponse> updateItem(@PathVariable("item-id") UUID itemId, @RequestBody @Valid ItemUpdateRequest request,
                                                   @AuthenticationPrincipal UserDetailsImpl user) {
        return ResponseEntity.status(HttpStatus.OK).body(
            itemService.updateItem(user.getUserId(), user.getUserRole(), itemId, request)
        );
    }

    @Operation(
        summary = "상품 삭제 요청 API",
        description = "사용자(OWNER, MANAGER, MASTER)가 상품을 삭제합니다. 해당 API 요청 권한은 OWNER, MANAGER, MASTER만 가능합니다.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = {
            @Parameter(name = "item-id", description = "삭제할 상품의 id value")
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "상품 삭제에 성공하였습니다."),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 요청입니다. (JWT 토큰 누락 또는 만료)", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "접근 권한이 없습니다. (OWNER, MANAGER, MASTER 아님)", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "상품 정보가 존재하지 않습니다.", content = @Content(mediaType = "application/json"))
        }
    )
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
    @DeleteMapping("/{item-id}")
    public ResponseEntity<Void> softDelete(@PathVariable("item-id") String itemId,
                                           @AuthenticationPrincipal UserDetailsImpl user) {
        itemService.softDelete(user.getUserId(), user.getUserRole(), UUID.fromString(itemId));
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(
        summary = "상품 복구 요청 API",
        description = "사용자(OWNER, MANAGER, MASTER)가 상품을 복구합니다. 해당 API 요청 권한은 OWNER, MANAGER, MASTER만 가능합니다.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = {
            @Parameter(name = "item-id", description = "복구할 상품의 id value")
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "상품 복구에 성공하였습니다."),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 요청입니다. (JWT 토큰 누락 또는 만료)", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "접근 권한이 없습니다. (OWNER, MANAGER, MASTER 아님)", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "상품 정보가 존재하지 않습니다.", content = @Content(mediaType = "application/json"))
        }
    )
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
    @PatchMapping("/{item-id}/restore")
    public ResponseEntity<Void> restoreItem(@PathVariable("item-id") String itemId,
                                            @AuthenticationPrincipal UserDetailsImpl user) {
        itemService.restoreItem(user.getUserId(), user.getUserRole(), UUID.fromString(itemId));
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(
        summary = "상품 숨김/해제 요청 API",
        description = "사용자(OWNER, MANAGER, MASTER)가 상품을 숨김/해제합니다. 해당 API 요청 권한은 OWNER, MANAGER, MASTER만 가능합니다.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = {
            @Parameter(name = "item-id", description = "숨김/해제할 상품의 id value")
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "상품 숨김/해제에 성공하였습니다."),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 요청입니다. (JWT 토큰 누락 또는 만료)", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "접근 권한이 없습니다. (OWNER, MANAGER, MASTER 아님)", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "상품 정보가 존재하지 않습니다.", content = @Content(mediaType = "application/json"))
        }
    )
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
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
