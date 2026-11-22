package com.nomevini.opengrana.account;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Locale;
import java.util.Objects;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Money {

    @Column(name = "amount", precision = 19, scale = 4, nullable = false)
    private BigDecimal amount;

    @Column(name = "currency", length = 3, nullable = false)
    private String currency;

    public Money(BigDecimal amount, String currency) {
        this.amount = Objects.requireNonNull(amount, "amount is required");
        Objects.requireNonNull(currency, "currency is required");

        String normalized = currency.trim().toUpperCase(Locale.ROOT);
        Currency.getInstance(normalized); // valida ISO 4217
        this.currency = normalized;
    }

    /** Valor com a escala da moeda (BRL = 2), sem notação científica. */
    public String formattedAmount() {
        int digits = Currency.getInstance(currency).getDefaultFractionDigits();
        return amount.setScale(digits, RoundingMode.HALF_UP).toPlainString();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Money money)) return false;
        return Objects.equals(amount, money.amount) && Objects.equals(currency, money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }
}