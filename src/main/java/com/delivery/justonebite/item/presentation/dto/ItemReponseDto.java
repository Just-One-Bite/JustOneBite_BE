package com.delivery.justonebite.item.presentation.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class ItemReponseDto {
    private UUID itemId;
    private String name;
    private int price;
    private String image;
    private boolean isHidden; // isHidden을 넣은 이유는 프론트가 알아서 뺴라고 넣었습니다. role에 따라서 보여야 할 수도 있겠다 싶어서 그랬습니다.
}
