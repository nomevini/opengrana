package com.nomevini.opengrana.account;

import com.nomevini.opengrana.account.dto.*;
import com.nomevini.opengrana.account.exception.*;
import com.nomevini.opengrana.user.User;
import com.nomevini.opengrana.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserService userService;

    public AccountService(AccountRepository accountRepository, UserService userService) {
        this.accountRepository = accountRepository;
        this.userService = userService;
    }

    @Transactional
    public AccountResponse create(String email, CreateAccountRequest request) {
        User user = userService.requireByEmail(email);
        String currencyCode = request.currency().trim().toUpperCase();
        int fractionDigits = fractionDigitsOf(currencyCode);
        BigDecimal amount = request.initialBalance().setScale(fractionDigits, RoundingMode.HALF_UP);

        Account account = new Account(
                user,
                request.name().trim(),
                request.type(),
                new Money(amount, currencyCode)
        );

        return toResponse(accountRepository.saveAndFlush(account));
    }

    public List<AccountResponse> list(String email) {
        User user = userService.requireByEmail(email);
        return accountRepository.findAllByUser_IdOrderByName(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public AccountResponse get(String email, UUID publicId) {
        return toResponse(findOwned(email, publicId));
    }

    @Transactional
    public AccountResponse update(String email, UUID publicId, String ifMatch, UpdateAccountRequest request) {
        Account account = findOwned(email, publicId);
        checkVersion(account, ifMatch);

        account.rename(request.name().trim());
        account.changeType(request.type());

        return toResponse(accountRepository.saveAndFlush(account));
    }

    @Transactional
    public void close(String email, UUID publicId, String ifMatch) {
        Account account = findOwned(email, publicId);
        checkVersion(account, ifMatch);
        account.close();
        accountRepository.saveAndFlush(account);
    }

    private Account findOwned(String email, UUID publicId) {
        User user = userService.requireByEmail(email);
        return accountRepository.findByPublicIdAndUser_Id(publicId, user.getId())
                .orElseThrow(AccountNotfoundException::new);
    }

    private void checkVersion(Account account, String ifMatch) {
        long expected = parseIfMatch(ifMatch);
        long actual = account.getVersion() == null ? 0L : account.getVersion();
        if (expected != actual) {
            throw new StaleAccountException(expected, actual);
        }
    }

    private long parseIfMatch(String header) {
        if (header == null || header.isBlank()) {
            throw new MissingIfMatchException();
        }
        String value = header.strip();
        if (value.startsWith("W/")) {
            value = value.substring(2).strip();
        }
        if (value.length() < 3 || !value.startsWith("\"") || !value.endsWith("\"")) {
            throw new InvalidIfMatchException();
        }
        try {
            return Long.parseLong(value.substring(1, value.length() - 1));
        } catch (NumberFormatException ex) {
            throw new InvalidIfMatchException();
        }
    }

    private int fractionDigitsOf(String currencyCode) {
        try {
            return Currency.getInstance(currencyCode).getDefaultFractionDigits();
        } catch (IllegalArgumentException ex) {
            throw new InvalidCurrencyException(currencyCode);
        }
    }

    private AccountResponse toResponse(Account account) {
        Money opening = account.getOpeningBalance();

        LocalDateTime asOf = account.getBalanceAsOf() != null
                ? account.getBalanceAsOf()
                : account.getCreatedAt();

        BalanceResponse balance = new BalanceResponse(
                opening.formattedAmount(),
                opening.getCurrency(),
                asOf,
                "MANUAL",
                false
        );

        long version = account.getVersion() == null ? 0L : account.getVersion();

        return new AccountResponse(
                account.getPublicId(),
                account.getName(),
                account.getType(),
                account.getStatus(),
                account.getSource().name(),
                balance,
                account.getCreatedAt(),
                version
        );
    }
}