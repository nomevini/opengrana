package com.nomevini.opengrana.account;

import com.nomevini.opengrana.user.Role;
import com.nomevini.opengrana.user.User;
import com.nomevini.opengrana.user.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
class AccountRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    @Autowired AccountRepository accountRepository;
    @Autowired UserRepository userRepository;
    @Autowired EntityManager entityManager;

    private User owner;

    @BeforeEach
    void setUp() {
        owner = userRepository.saveAndFlush(User.builder()
                .name("Owner").email("owner@email.com").password("hash").role(Role.USER).build());
    }

    private Account account(String name) {
        return new Account(owner, name, AccountType.CHECKING,
                new Money(new BigDecimal("1500.00"), "BRL"));
    }

    @Test
    void shouldPersistWithDatabaseGeneratedFields() {
        Account saved = accountRepository.saveAndFlush(account("Nubank"));
        entityManager.refresh(saved);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getPublicId()).isNotNull();
        assertThat(saved.getVersion()).isZero();
        assertThat(saved.getStatus()).isEqualTo(AccountStatus.ACTIVE);
        assertThat(saved.getSource()).isEqualTo(AccountSource.MANUAL);
        assertThat(saved.getExternalId()).isNull();
        assertThat(saved.getItemId()).isNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
        assertThat(saved.getBalanceAsOf()).isNotNull();
        assertThat(saved.getOpeningBalance().getAmount()).isEqualByComparingTo("1500.00");
        assertThat(saved.getOpeningBalance().getCurrency()).isEqualTo("BRL");
    }

    @Test
    void shouldNotChangeBalanceTimestampWhenRenaming() {
        Account saved = accountRepository.saveAndFlush(account("Nubank"));
        LocalDateTime before = saved.getBalanceAsOf();

        saved.rename("Nubank 2");
        entityManager.flush();
        entityManager.refresh(saved);

        assertThat(saved.getBalanceAsOf()).isEqualToIgnoringNanos(before);
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldListOnlyAccountsOfOwnerOrderedByName() {
        accountRepository.saveAndFlush(account("Zebra"));
        accountRepository.saveAndFlush(account("Alfa"));

        List<Account> accounts = accountRepository.findAllByUser_IdOrderByName(owner.getId());

        assertThat(accounts).extracting(Account::getName).containsExactly("Alfa", "Zebra");
    }

    @Test
    void shouldNotFindAccountOfAnotherUser() {
        User other = userRepository.saveAndFlush(User.builder()
                .name("Other").email("other@email.com").password("hash").role(Role.USER).build());
        Account foreign = accountRepository.saveAndFlush(new Account(other, "Itaú",
                AccountType.SAVINGS, new Money(new BigDecimal("10.00"), "BRL")));

        assertThat(accountRepository.findByIdAndUser_Id(foreign.getId(), owner.getId())).isEmpty();
        assertThat(accountRepository.findByPublicIdAndUser_Id(foreign.getPublicId(), owner.getId())).isEmpty();
    }
}
