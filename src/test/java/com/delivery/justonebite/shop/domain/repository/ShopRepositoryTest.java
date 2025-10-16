package com.delivery.justonebite.shop.domain.repository;

import com.delivery.justonebite.shop.domain.entity.Shop;
import com.delivery.justonebite.shop.projection.ShopAvgProjection;
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
import org.springframework.test.annotation.Commit;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ShopRepositoryTest {

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private UserRepository userRepository;

    private User owner;

    @BeforeEach
    void setUp(){
        //테스트용 유저
        owner = userRepository.save(
                User.builder()
                        .email("owner@gmail.com")
                        .name("고객")
                        .phoneNumber("010-1345-6789")
                        .password("Owner1234!")
                        .userRole(UserRole.OWNER)
                        .build()

        );



        //테스트용 가게 저장
        shopRepository.save(
                Shop.builder()
                        .ownerId(owner.getId())
                        .name("맛있는 치킨집")
                        .registrationNumber("123-45-6789")
                        .province("서울특별시")
                        .city("종로구")
                        .district("낙원동")
                        .address("종로 1길 23")
                        .phoneNumber("02-1234-1234")
                        .description("바삭한 후라이드")
                        .operatingHour("10:00 - 22:00")
                        .createdBy(owner.getId())
                        .updatedBy(owner.getId())
                        .build()
        );


    }


    @Test
    @DisplayName("Shop 평균 리뷰 평점 조회")
    void findAvgByIds() {
        List<UUID> ids = shopRepository.findAll().stream().map(Shop::getId).toList();

        List<ShopAvgProjection> result = shopRepository.findAvgByIds(ids);

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getAverageRating()).isEqualByComparingTo(BigDecimal.valueOf(0.0));

    }

    @Test
    @DisplayName("가게 평점 업데이트 ")
    void bulkUpdateAllAvg() {
        int updated = shopRepository.bulkUpdateAllAvg();
        assertThat(updated).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("리뷰 없는 가게를 0점으로 업데이트")
    void bulkResetAvgForZeroReview() {
        int updated = shopRepository.bulkResetAvgForZeroReview();
        assertThat(updated).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("이름, 설명으로 가게 검색")
    void findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase() {
        Page<Shop> page = shopRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                "치킨", "바삭", PageRequest.of(0, 10)
        );

        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent().get(0).getName()).contains("치킨");
    }

    @Test
    @DisplayName("삭제되지 않은 가게 조회")
    void findByIdAndDeletedAtIsNull() {
        Shop shop = shopRepository.findAll().get(0);

        Optional<Shop> notDeleted = shopRepository.findByIdAndDeletedAtIsNull(shop.getId());

        assertThat(notDeleted).isPresent();
        assertThat(notDeleted.get().getName()).isEqualTo(shop.getName());
    }
}
