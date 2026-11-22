package com.nomevini.opengrana.account.dto;

import com.nomevini.opengrana.account.AccountStatus;
import com.nomevini.opengrana.account.AccountType;

import java.time.LocalDateTime;
import java.util.UUID;

public record AccountResponse(
        UUID publicId,
        String name,
        AccountType type,
        AccountStatus status,
        String source,
        BalanceResponse balance,
        LocalDateTime createdAt,
        long version
) {
}
