package com.delivery.justonebite.item.domain.entity;

import com.delivery.justonebite.common.entity.BaseEntity;
import com.delivery.justonebite.item.presentation.dto.ItemDetailResponseDto;
import com.delivery.justonebite.item.presentation.dto.ItemReponseDto;
import com.delivery.justonebite.item.presentation.dto.ItemUpdateRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;


@Getter
@Entity
@SuperBuilder
@NoArgsConstructor
@Table(name = "h_item")
public class ItemEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private UUID itemId;

    @Column(name = "shop_id", nullable = false)
    private UUID shopId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int price;

    @Column
    private String image;

    @Column
    private String description;

    @Column(name = "ai_generated", nullable = false)
    private boolean aiGenerated;

    @Column(name = "is_hidden", nullable = false)
    private boolean isHidden;

    public void updateItem(ItemUpdateRequestDto requestDto) {
        this.name = requestDto.getName();
        this.price = requestDto.getPrice();
        this.image = requestDto.getImage();
        this.description = requestDto.getDescription();
        this.aiGenerated = requestDto.isAiGenerated();
    }

    public void toggleIsHidden() {
        this.isHidden = !this.isHidden;
    }

    public ItemReponseDto toItemReponseDto() {
        return ItemReponseDto.builder()
            .itemId(itemId)
            .name(name)
            .price(price)
            .image(image)
            .isHidden(isHidden)
            .build();
    }

    public ItemDetailResponseDto toItemDetailResponseDto() {
        return ItemDetailResponseDto.builder()
            .itemId(itemId)
            .name(name)
            .price(price)
            .image(image)
            .description(description)
            .isHidden(isHidden)
            .build();
    }
}
