package com.delivery.justonebite.item.domain.repository;

import com.delivery.justonebite.item.domain.entity.Item;
import com.delivery.justonebite.item.presentation.dto.ItemUpdateRequest;
import com.delivery.justonebite.shop.domain.entity.Shop;
import com.delivery.justonebite.shop.domain.repository.ShopRepository;
import com.delivery.justonebite.user.domain.entity.User;
import com.delivery.justonebite.user.domain.entity.UserRole;
import com.delivery.justonebite.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private Long userId;
    private UUID shopId;
    private Shop shop;

    @BeforeEach
    void setUp() {
        user = User.builder()
            .name("Jack")
            .email("jack@example.com")
            .password("password")
            .phoneNumber("010-0000-0000")
            .userRole(UserRole.MASTER)
            .build();

        User savedUser = userRepository.save(user);
        userId = savedUser.getId();

        shop = Shop.builder()
            .ownerId(userId)
            .name("Shop")
            .registrationNumber("registrationNumber")
            .province("province")
            .city("city")
            .district("district")
            .address("address")
            .phoneNumber("010-0000-0000")
            .operatingHour("operatingHour")
            .createdAt(LocalDateTime.now())
            .createdBy(1L)
            .updatedAt(LocalDateTime.now())
            .updatedBy(1L)
            .build();

        Shop savedShop = shopRepository.save(shop);
        shopId = savedShop.getId();
    }

    @Test
    @DisplayName("save")
    void save() {
        Item item = Item.builder()
            .shop(shop)
            .name("김치찜")
            .price(15000)
            .image("image")
            .description("맛있는 김치찜")
            .aiGenerated(false)
            .isHidden(false)
            .createdAt(LocalDateTime.now())
            .createdBy(1L)
            .updatedAt(LocalDateTime.now())
            .updatedBy(1L)
            .build();

        Item saveItem = itemRepository.save(item);

        assertThat(saveItem.getItemId()).isNotNull();
        assertThat(saveItem.getName()).isEqualTo("김치찜");
        assertThat(saveItem.getPrice()).isEqualTo(15000);
        assertThat(saveItem.getImage()).isEqualTo("image");
        assertThat(saveItem.getDescription()).isEqualTo("맛있는 김치찜");
        assertThat(saveItem.isAiGenerated()).isEqualTo(false);
        assertThat(saveItem.isHidden()).isEqualTo(false);
    }

    @Test
    @DisplayName("findByItemId")
    void findByItemId() {
        Item item = Item.builder()
            .shop(shop)
            .name("김치찜")
            .price(15000)
            .image("image")
            .description("맛있는 김치찜")
            .aiGenerated(false)
            .isHidden(false)
            .createdAt(LocalDateTime.now())
            .createdBy(1L)
            .updatedAt(LocalDateTime.now())
            .updatedBy(1L)
            .build();

        Item saveItem = itemRepository.save(item);
        UUID itemId = saveItem.getItemId();

        Optional<Item> tempItem = itemRepository.findById(itemId);

        Item findItem = tempItem.get();

        assertThat(findItem.getItemId()).isEqualTo(itemId);
        assertThat(findItem.getName()).isEqualTo("김치찜");
        assertThat(findItem.getPrice()).isEqualTo(15000);
        assertThat(findItem.getImage()).isEqualTo("image");
        assertThat(findItem.getDescription()).isEqualTo("맛있는 김치찜");
        assertThat(findItem.isAiGenerated()).isEqualTo(false);
        assertThat(findItem.isHidden()).isEqualTo(false);
    }

    @Test
    @DisplayName("findAllByItemIdIn")
    void findAllByItemIdIn() {
        List<UUID> itemIds = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Item item = Item.builder()
                .shop(shop)
                .name("김치찜")
                .price(15000)
                .image("image")
                .description("맛있는 김치찜")
                .aiGenerated(false)
                .isHidden(false)
                .createdAt(LocalDateTime.now())
                .createdBy(1L)
                .updatedAt(LocalDateTime.now())
                .updatedBy(1L)
                .build();

            Item saveItem = itemRepository.save(item);
            UUID itemId = saveItem.getItemId();

            itemIds.add(itemId);
        }

        List<Item> items = itemRepository.findAllByItemIdIn(itemIds);

        assertThat(items.size()).isEqualTo(10);
    }

    @Test
    @DisplayName("findByItemIdWithoutHidden")
    void findByItemIdWithoutHidden() {
        Item item = Item.builder()
            .shop(shop)
            .name("김치찜")
            .price(15000)
            .image("image")
            .description("맛있는 김치찜")
            .aiGenerated(false)
            .isHidden(true)
            .createdAt(LocalDateTime.now())
            .createdBy(1L)
            .updatedAt(LocalDateTime.now())
            .updatedBy(1L)
            .build();

        Item saveItem = itemRepository.save(item);
        UUID itemId = saveItem.getItemId();

        Optional<Item> tempItem = itemRepository.findByItemIdWithoutHidden(itemId);

        assertThat(tempItem.isPresent()).isFalse();
    }

    @Test
    @DisplayName("findAllByShopIdWithoutHidden")
    void findAllByShopIdWithoutHidden() {
        for (int i = 0; i < 10; i++) {
            Item item = Item.builder()
                .shop(shop)
                .name("김치찜")
                .price(15000)
                .image("image")
                .description("맛있는 김치찜")
                .aiGenerated(false)
                .isHidden(i >= 5)
                .createdAt(LocalDateTime.now())
                .createdBy(1L)
                .updatedAt(LocalDateTime.now())
                .updatedBy(1L)
                .build();

            itemRepository.save(item);
        }

        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Page<Item> items = itemRepository.findAllByShopIdWithoutHidden(shopId, pageable);

        assertThat(items.getContent()).hasSize(5);
    }

    @Test
    @DisplayName("findByItemIdWithNativeQuery")
    void findByItemIdWithNativeQuery() {
        Item item = Item.builder()
            .shop(shop)
            .name("김치찜")
            .price(15000)
            .image("image")
            .description("맛있는 김치찜")
            .aiGenerated(false)
            .isHidden(true)
            .createdAt(LocalDateTime.now())
            .createdBy(1L)
            .updatedAt(LocalDateTime.now())
            .updatedBy(1L)
            .build();

        Item saveItem = itemRepository.save(item);
        UUID itemId = saveItem.getItemId();

        Optional<Item> tempItem = itemRepository.findByItemIdWithNativeQuery(itemId);

        assertThat(tempItem.isPresent()).isTrue();
    }

    @Test
    @DisplayName("findAllByShopIdWithNativeQuery")
    void findAllByShopIdWithNativeQuery() {
        for (int i = 0; i < 10; i++) {
            Item item = Item.builder()
                .shop(shop)
                .name("김치찜")
                .price(15000)
                .image("image")
                .description("맛있는 김치찜")
                .aiGenerated(false)
                .isHidden(i >= 5)
                .createdAt(LocalDateTime.now())
                .createdBy(1L)
                .updatedAt(LocalDateTime.now())
                .updatedBy(1L)
                .build();

            itemRepository.save(item);
        }

        Pageable pageable = PageRequest.of(0, 10, Sort.by("created_at").descending());
        Page<Item> items = itemRepository.findAllByShopIdWithNativeQuery(shopId, pageable);

        assertThat(items.getContent()).hasSize(10);
    }

    @Test
    @DisplayName("update")
    void update() {
        Item item = Item.builder()
            .shop(shop)
            .name("김치찜")
            .price(13000)
            .image("image")
            .description("맛없는 김치찜")
            .aiGenerated(false)
            .isHidden(false)
            .createdAt(LocalDateTime.now())
            .createdBy(1L)
            .updatedAt(LocalDateTime.now())
            .updatedBy(1L)
            .build();

        Item saveItem = itemRepository.save(item);

        ItemUpdateRequest itemRequest = new ItemUpdateRequest(
            "김치찜",
            15000,
            "image",
            "맛있는 김치찜",
            false
        );

        saveItem.updateItem(itemRequest);

        assertThat(saveItem.getItemId()).isNotNull();
        assertThat(saveItem.getName()).isEqualTo("김치찜");
        assertThat(saveItem.getPrice()).isEqualTo(15000);
        assertThat(saveItem.getImage()).isEqualTo("image");
        assertThat(saveItem.getDescription()).isEqualTo("맛있는 김치찜");
        assertThat(saveItem.isAiGenerated()).isEqualTo(false);
        assertThat(saveItem.isHidden()).isEqualTo(false);
    }

    @Test
    @DisplayName("delete failed")
    void delete() {
        Item item = Item.builder()
            .shop(shop)
            .name("김치찜")
            .price(13000)
            .image("image")
            .description("맛없는 김치찜")
            .aiGenerated(false)
            .isHidden(false)
            .createdAt(LocalDateTime.now())
            .createdBy(1L)
            .updatedAt(LocalDateTime.now())
            .updatedBy(1L)
            .build();

        Item saveItem = itemRepository.save(item);

        assertThatThrownBy(() -> itemRepository.delete(saveItem))
            .isInstanceOf(UnsupportedOperationException.class)
            .hasFieldOrPropertyWithValue("message", "Use service.softDelete(...) instead.");
    }
}