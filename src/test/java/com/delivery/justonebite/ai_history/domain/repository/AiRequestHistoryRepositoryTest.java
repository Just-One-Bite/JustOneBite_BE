package com.delivery.justonebite.ai_history.domain.repository;

import com.delivery.justonebite.ai_history.domain.entity.AiRequestHistory;
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

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AiRequestHistoryRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AiRequestHistoryRepository aiRequestHistoryRepository;

    private User user;
    private Long userId;

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
    }

    @Test
    @DisplayName("save")
    void save() {
        AiRequestHistory aiRequestHistory = new AiRequestHistory(user, "gemini", "hi", "bye");

        AiRequestHistory saveHistory = aiRequestHistoryRepository.save(aiRequestHistory);
        assertThat(saveHistory.getId()).isNotNull();
        assertThat(saveHistory.getUser()).isEqualTo(user);
        assertThat(saveHistory.getModel()).isEqualTo("gemini");
        assertThat(saveHistory.getRequest()).isEqualTo("hi");
        assertThat(saveHistory.getResponse()).isEqualTo("bye");
    }

    @Test
    @DisplayName("find by id")
    void findById() {
        AiRequestHistory aiRequestHistory = new AiRequestHistory(user, "gemini", "hi", "bye");

        AiRequestHistory saveHistory = aiRequestHistoryRepository.save(aiRequestHistory);
        UUID id = saveHistory.getId();

        Optional<AiRequestHistory> found = aiRequestHistoryRepository.findById(id);
        assertThat(found.isPresent()).isTrue();
        assertThat(found.get().getUser()).isEqualTo(user);
        assertThat(found.get().getModel()).isEqualTo("gemini");
        assertThat(found.get().getRequest()).isEqualTo("hi");
        assertThat(found.get().getResponse()).isEqualTo("bye");
    }

    @Test
    @DisplayName("find all by user")
    void findAllByUser() {
        AiRequestHistory aiRequestHistory1 = new AiRequestHistory(user, "gemini", "hi", "bye");
        AiRequestHistory aiRequestHistory2 = new AiRequestHistory(user, "gemini", "hi", "bye");

        aiRequestHistoryRepository.save(aiRequestHistory1);
        aiRequestHistoryRepository.save(aiRequestHistory2);

        Pageable pageable = PageRequest.of(0, 10);

        Page<AiRequestHistory> found = aiRequestHistoryRepository.findAllByUser(user, pageable);

        assertThat(found).isNotNull();
        assertThat(found.getTotalElements()).isEqualTo(2);
    }
}