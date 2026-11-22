package com.nomevini.opengrana.account.dto;

import com.nomevini.opengrana.account.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateAccountRequest(
        @NotBlank @Size(max = 150) String name,
        @NotNull AccountType type
) {
}
