package com.nomevini.opengrana.account.dto;

import java.time.LocalDateTime;

public record BalanceResponse(
        String amount,
        String currency,
        LocalDateTime asOf,
        String source, // MANUAL | PLUGGY_SYNC | PLUGGY_REALTIME
        boolean stale
) {
}
