package com.delivery.justonebite.item.domain.entity;

import com.delivery.justonebite.global.common.entity.BaseEntity;
import com.delivery.justonebite.item.presentation.dto.ItemUpdateRequest;
import com.delivery.justonebite.shop.domain.entity.Shop;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;


@Getter
@Entity
@SuperBuilder
@NoArgsConstructor
@Table(name = "h_item")
public class Item extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "item_id")
    private UUID itemId;


    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

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

    public void updateDescription(String aiResponse) {
        this.description = aiResponse;
    }

    public void updateItem(ItemUpdateRequest request) {
        this.name = request.name();
        this.price = request.price();
        this.image = request.image();
        this.description = request.description();
        this.aiGenerated = request.aiGenerated();
    }

    public void toggleIsHidden() {
        this.isHidden = !this.isHidden;
    }
}
