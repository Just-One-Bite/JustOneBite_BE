package com.delivery.justonebite.item.presentation.dto.request;

import com.delivery.justonebite.item.domain.entity.Item;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Schema(description = "상품 생성 요청 DTO")
@Builder
public record ItemRequest(
    @Schema(description = "주문할 가게의 고유 ID", example = "4a94970e-1a5c-4e89-9a28-661074e50d0a")
    @NotNull(message = "가게 ID는 필수입니다")
    String shopId,

    @Schema(description = "등록할 상품의 상품명", example = "김치찜")
    @NotBlank(message = "상품 명은 공백일 수 없습니다.")
    String name,

    @Schema(description = "등록할 상품의 가격", example = "15000")
    @Min(value = 10, message = "상품 금액은 10원 이상이어야 합니다.")
    int price,

    @Schema(description = "등록할 상품의 image")
    String image,

    @Schema(description = "등록할 상품의 설명, 상품 설명 AI 생성 시 상품에 대한 프롬프트", example = "맛있는 김치찜")
    @Size(max = 100)
    String description,

    @Schema(description = "AI 상품 설명 API 이용 여부")
    boolean aiGenerated
) {
    public Item toItem() {
        return Item.builder()
            .name(name)
            .price(price)
            .image(image)
            .description(description)
            .aiGenerated(aiGenerated)
            .isHidden(false)
            .build();
    }
}