package com.nomevini.opengrana.account.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nomevini.opengrana.account.AccountType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateAccountRequest(
        @NotBlank @Size(max = 150) String name,
        @NotNull AccountType type,

        @NotNull
        @Digits(integer = 15, fraction = 4)
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        BigDecimal initialBalance,

        @NotBlank
        @Size(min = 3, max = 3)
        @Pattern(regexp = "^[A-Z]{3}$")
        String currency
) {
}
