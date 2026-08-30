package com.nomevini.opengrana.user;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
class UserRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldSaveUserWithIdAndTimestampsGeneratedByDatabase() {
        User savedUser = userRepository.saveAndFlush(User.builder()
                .name("Test User")
                .email("test@email.com")
                .password("hash")
                .role(Role.USER)
                .build());

        entityManager.refresh(savedUser);

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.isActive()).isTrue();
        assertThat(savedUser.getCreatedAt()).isNotNull();
        assertThat(savedUser.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldFindUserByEmail() {
        User user = userRepository.saveAndFlush(User.builder()
                .name("Maria")
                .email("maria@email.com")
                .password("hash")
                .role(Role.ADMIN)
                .build());

        assertThat(userRepository.existsByEmail("maria@email.com")).isTrue();
        assertThat(userRepository.findByEmail("maria@email.com"))
                .hasValueSatisfying(foundUser -> {
                    assertThat(foundUser.getId()).isEqualTo(user.getId());
                    assertThat(foundUser.getName()).isEqualTo("Maria");
                    assertThat(foundUser.getRole()).isEqualTo(Role.ADMIN);
                });
    }
}