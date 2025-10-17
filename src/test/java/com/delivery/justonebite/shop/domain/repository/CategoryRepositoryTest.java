package com.delivery.justonebite.shop.domain.repository;

import com.delivery.justonebite.shop.domain.entity.Category;
import com.delivery.justonebite.shop.domain.entity.Shop;
import com.delivery.justonebite.user.domain.entity.User;
import com.delivery.justonebite.user.domain.entity.UserRole;
import com.delivery.justonebite.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Commit;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

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

        // 테스트용 카테고리 데이터 삽입
        categoryRepository.saveAll(List.of(
                Category.builder().categoryName("치킨").build(),
                Category.builder().categoryName("피자").build(),
                Category.builder().categoryName("분식").build()
        ));


    }
    @Test
    @DisplayName("여러 카테고리로 조회")
    void findAllByCategoryNameIn() {
        List<String> categoryNames = List.of("치킨","피자");

        List<Category> categories = categoryRepository.findAllByCategoryNameIn(categoryNames);

        assertThat(categories).hasSize(2);
        assertThat(categories)
                .extracting(Category::getCategoryName)
                .containsExactlyInAnyOrder("치킨","피자");
    }

    @Test
    @DisplayName("한 카테고리 조회")
    void findByCategoryName() {
        Optional<Category> category = categoryRepository.findByCategoryName("분식");

        assertThat(category).isPresent();
        assertThat(category.get().getCategoryName()).isEqualTo("분식");
    }

    @Test
    @DisplayName("존재하지 않는 카테고리 조회")
    void findByCategoryName_notFound() {
        Optional<Category> category = categoryRepository.findByCategoryName("패스트푸드");

        assertThat(category).isEmpty();
    }
}