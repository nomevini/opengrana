package com.nomevini.opengrana.account;

import com.nomevini.opengrana.account.dto.AccountResponse;
import com.nomevini.opengrana.account.dto.BalanceResponse;
import com.nomevini.opengrana.account.dto.CreateAccountRequest;
import com.nomevini.opengrana.account.dto.UpdateAccountRequest;
import com.nomevini.opengrana.account.exception.AccountNotfoundException;
import com.nomevini.opengrana.account.exception.StaleAccountException;
import com.nomevini.opengrana.exception.GlobalExceptionHandler;
import com.nomevini.opengrana.support.ArgumentResolverConfig;
import com.nomevini.opengrana.support.SecurityTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
@Import({GlobalExceptionHandler.class, ArgumentResolverConfig.class})
class AccountControllerTest extends SecurityTestSupport {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockitoBean AccountService accountService;

    private AccountResponse response(UUID publicId) {
        LocalDateTime at = LocalDateTime.of(2026, 11, 20, 10, 0);
        return new AccountResponse(publicId, "Nubank", AccountType.CHECKING, AccountStatus.ACTIVE,
                "MANUAL",
                new BalanceResponse("1500.00", "BRL", at, "MANUAL", false),
                at, 3L);
    }

    @Test
    void shouldCreateAccount() throws Exception {
        UUID publicId = UUID.randomUUID();
        when(accountService.create(eq("user@email.com"), any())).thenReturn(response(publicId));

        var request = new CreateAccountRequest("Nubank", AccountType.CHECKING,
                new BigDecimal("1500.00"), "BRL");

        mockMvc.perform(post("/accounts")
                        .with(withJwt("user@email.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString(publicId.toString())))
                .andExpect(header().string("ETag", "\"3\""))
                .andExpect(jsonPath("$.publicId").value(publicId.toString()))
                .andExpect(jsonPath("$.source").value("MANUAL"))
                .andExpect(jsonPath("$.balance.amount").value("1500.00"))
                .andExpect(jsonPath("$.balance.currency").value("BRL"));
    }

    @Test
    void shouldListAccounts() throws Exception {
        when(accountService.list("user@email.com"))
                .thenReturn(List.of(response(UUID.randomUUID())));

        mockMvc.perform(get("/accounts").with(withJwt("user@email.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Nubank"));
    }

    @Test
    void shouldGetAccountWithEtag() throws Exception {
        UUID publicId = UUID.randomUUID();
        when(accountService.get("user@email.com", publicId)).thenReturn(response(publicId));

        mockMvc.perform(get("/accounts/{publicId}", publicId).with(withJwt("user@email.com")))
                .andExpect(status().isOk())
                .andExpect(header().string("ETag", "\"3\""));
    }

    @Test
    void shouldReturn404WhenServiceThrowsAccountNotFound() throws Exception {
        UUID publicId = UUID.randomUUID();
        when(accountService.get("user@email.com", publicId))
                .thenThrow(new AccountNotfoundException());

        mockMvc.perform(get("/accounts/{publicId}", publicId).with(withJwt("user@email.com")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Account not found"))
                .andExpect(jsonPath("$.type").value("https://opengrana.dev/problems/account-not-found"));
    }

    @Test
    void shouldPatchWithIfMatch() throws Exception {
        UUID publicId = UUID.randomUUID();
        when(accountService.update(eq("user@email.com"), eq(publicId), eq("\"3\""), any()))
                .thenReturn(response(publicId));

        var body = new UpdateAccountRequest("Nubank", AccountType.SAVINGS);

        mockMvc.perform(patch("/accounts/{publicId}", publicId)
                        .with(withJwt("user@email.com"))
                        .header("If-Match", "\"3\"")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(header().string("ETag", "\"3\""));
    }

    @Test
    void shouldReturn412WhenVersionIsStale() throws Exception {
        UUID publicId = UUID.randomUUID();
        when(accountService.update(anyString(), eq(publicId), anyString(), any()))
                .thenThrow(new StaleAccountException(3, 4));

        mockMvc.perform(patch("/accounts/{publicId}", publicId)
                        .with(withJwt("user@email.com"))
                        .header("If-Match", "\"3\"")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UpdateAccountRequest("Nubank", AccountType.SAVINGS))))
                .andExpect(status().isPreconditionFailed())
                .andExpect(jsonPath("$.title").value("Precondition failed"));
    }

    @Test
    void shouldReturn204OnDelete() throws Exception {
        UUID publicId = UUID.randomUUID();
        doNothing().when(accountService).close("user@email.com", publicId, "\"3\"");

        mockMvc.perform(delete("/accounts/{publicId}", publicId)
                        .with(withJwt("user@email.com"))
                        .header("If-Match", "\"3\""))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn400ForBlankName() throws Exception {
        mockMvc.perform(post("/accounts")
                        .with(withJwt("user@email.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateAccountRequest("", AccountType.CHECKING,
                                        new BigDecimal("10.00"), "BRL"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("name"));
    }

    @Test
    void shouldReturn400ForInvalidEnum() throws Exception {
        mockMvc.perform(post("/accounts")
                        .with(withJwt("user@email.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"X\",\"type\":\"INVALID\",\"initialBalance\":\"10.00\",\"currency\":\"BRL\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid request"));
    }

    @Test
    void shouldReturn400ForInvalidUuid() throws Exception {
        mockMvc.perform(get("/accounts/not-a-uuid").with(withJwt("user@email.com")))
                .andExpect(status().isBadRequest());
    }
}
