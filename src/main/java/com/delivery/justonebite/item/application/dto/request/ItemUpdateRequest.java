package com.delivery.justonebite.item.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "상품 수정 요청 DTO")
public record ItemUpdateRequest(
    @Schema(description = "수정할 상품의 상품명", example = "김치찜")
    @NotBlank(message = "상품 명은 공백일 수 없습니다.")
    String name,

    @Schema(description = "수정할 상품의 가격", example = "15000")
    @Min(value = 10, message = "상품 금액은 10원 이상이어야 합니다.")
    int price,

    @Schema(description = "수정할 상품의 image")
    String image,

    @Schema(description = "수정할 상품의 설명, 상품 설명 AI 생성 시 상품에 대한 프롬프트", example = "맛있는 김치찜")
    @Size(max = 100)
    String description,

    @Schema(description = "AI 상품 설명 API 이용 여부")
    boolean aiGenerated
) {

}
