package com.delivery.justonebite.shop.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import java.util.List;

@Schema(description = "가게 정보 수정 요청 DTO")
@Builder
public record ShopUpdateRequest(

        @Schema(description = "수정할 가게명", example = "바삭한 통닭집", nullable = true)
        @Size(max = 50)
        String name,

        @Schema(description = "수정할 전화번호", example = "02-987-6543", nullable = true)
        @Size(max = 20)
        String phone_number,

        @Schema(description = "수정할 영업시간", example = "평일 11:00 ~ 21:00", nullable = true)
        @Size(max = 100)
        String operating_hour,

        @Schema(description = "가게 설명", example = "매일 신선한 재료로 튀깁니다.", nullable = true)
        @Size(max = 1000)
        String description,

        @Schema(description = "수정할 카테고리 목록", example = "[\"치킨\", \"야식\"]", nullable = true)
        List<@Size(max = 10) String> categories
) {}
