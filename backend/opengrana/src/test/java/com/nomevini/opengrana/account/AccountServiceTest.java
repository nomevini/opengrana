package com.nomevini.opengrana.account;

import com.nomevini.opengrana.account.dto.CreateAccountRequest;
import com.nomevini.opengrana.account.dto.AccountResponse;
import com.nomevini.opengrana.account.dto.UpdateAccountRequest;
import com.nomevini.opengrana.account.exception.AccountNotfoundException;
import com.nomevini.opengrana.account.exception.MissingIfMatchException;
import com.nomevini.opengrana.account.exception.StaleAccountException;
import com.nomevini.opengrana.user.Role;
import com.nomevini.opengrana.user.User;
import com.nomevini.opengrana.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock AccountRepository accountRepository;
    @Mock UserService userService;
    @InjectMocks AccountService accountService;

    private User owner;

    @BeforeEach
    void setUp() {
        owner = User.builder().id(1L).name("Owner").email("owner@email.com")
                .password("hash").role(Role.USER).build();
    }

    private Account account(UUID publicId, long version) {
        Account account = new Account(owner, "Nubank", AccountType.CHECKING,
                new Money(new BigDecimal("1500.00"), "BRL"));
        ReflectionTestUtils.setField(account, "publicId", publicId);
        ReflectionTestUtils.setField(account, "version", version);
        ReflectionTestUtils.setField(account, "createdAt", LocalDateTime.of(2026, 11, 20, 10, 0));
        ReflectionTestUtils.setField(account, "balanceAsOf", LocalDateTime.of(2026, 11, 20, 10, 0));
        return account;
    }

    @Test
    void shouldCreateAccountForAuthenticatedUserWithCurrencyScale() {
        when(userService.requireByEmail("owner@email.com")).thenReturn(owner);
        when(accountRepository.saveAndFlush(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        AccountResponse response = accountService.create("owner@email.com",
                new CreateAccountRequest("Nubank", AccountType.CHECKING,
                        new BigDecimal("1500.005"), "brl"));

        assertThat(response.name()).isEqualTo("Nubank");
        assertThat(response.source()).isEqualTo("MANUAL");
        assertThat(response.balance().amount()).isEqualTo("1500.01");
        assertThat(response.balance().currency()).isEqualTo("BRL");
        assertThat(response.balance().source()).isEqualTo("MANUAL");
        assertThat(response.balance().stale()).isFalse();
        verify(accountRepository).saveAndFlush(argThat(a -> a.getUserId().equals(1L)));
    }

    @Test
    void shouldThrowWhenAccountBelongsToAnotherUser() {
        UUID publicId = UUID.randomUUID();
        when(userService.requireByEmail("owner@email.com")).thenReturn(owner);
        when(accountRepository.findByPublicIdAndUser_Id(publicId, 1L)).thenReturn(Optional.empty());

        assertThrows(AccountNotfoundException.class,
                () -> accountService.get("owner@email.com", publicId));
    }

    @Test
    void shouldUpdateOnlyEditableFields() {
        UUID publicId = UUID.randomUUID();
        Account account = account(publicId, 3L);
        when(userService.requireByEmail("owner@email.com")).thenReturn(owner);
        when(accountRepository.findByPublicIdAndUser_Id(publicId, 1L)).thenReturn(Optional.of(account));
        when(accountRepository.saveAndFlush(account)).thenReturn(account);

        accountService.update("owner@email.com", publicId, "\"3\"",
                new UpdateAccountRequest(" Novo nome ", AccountType.SAVINGS));

        assertThat(account.getName()).isEqualTo("Novo nome");
        assertThat(account.getType()).isEqualTo(AccountType.SAVINGS);
        assertThat(account.getOpeningBalance().getAmount()).isEqualByComparingTo("1500.00");
        assertThat(account.getBalanceAsOf()).isEqualTo(LocalDateTime.of(2026, 11, 20, 10, 0));
    }

    @Test
    void shouldRejectStaleVersionWithoutSaving() {
        UUID publicId = UUID.randomUUID();
        Account account = account(publicId, 4L);
        when(userService.requireByEmail("owner@email.com")).thenReturn(owner);
        when(accountRepository.findByPublicIdAndUser_Id(publicId, 1L)).thenReturn(Optional.of(account));

        assertThrows(StaleAccountException.class, () -> accountService.update(
                "owner@email.com", publicId, "\"3\"",
                new UpdateAccountRequest("Nome", AccountType.SAVINGS)));

        verify(accountRepository, never()).saveAndFlush(any());
    }

    @Test
    void shouldRejectMissingIfMatch() {
        UUID publicId = UUID.randomUUID();
        when(userService.requireByEmail("owner@email.com")).thenReturn(owner);
        when(accountRepository.findByPublicIdAndUser_Id(publicId, 1L))
                .thenReturn(Optional.of(account(publicId, 3L)));

        assertThrows(MissingIfMatchException.class, () -> accountService.update(
                "owner@email.com", publicId, null,
                new UpdateAccountRequest("Nome", AccountType.SAVINGS)));
    }

    @Test
    void shouldCloseInsteadOfDelete() {
        UUID publicId = UUID.randomUUID();
        Account account = account(publicId, 3L);
        when(userService.requireByEmail("owner@email.com")).thenReturn(owner);
        when(accountRepository.findByPublicIdAndUser_Id(publicId, 1L)).thenReturn(Optional.of(account));
        when(accountRepository.saveAndFlush(account)).thenReturn(account);

        accountService.close("owner@email.com", publicId, "\"3\"");

        assertThat(account.getStatus()).isEqualTo(AccountStatus.CLOSED);
        assertThat(account.getClosedAt()).isNotNull();
        verify(accountRepository, never()).delete(any());
    }
}
