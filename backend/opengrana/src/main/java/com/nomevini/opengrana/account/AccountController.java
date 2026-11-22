package com.nomevini.opengrana.account;

import com.nomevini.opengrana.account.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> create(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateAccountRequest request) {

        AccountResponse body = accountService.create(jwt.getSubject(), request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{publicId}")
                .buildAndExpand(body.publicId())
                .toUri();

        return ResponseEntity.created(location)
                .eTag(etag(body.version()))
                .body(body);
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> list(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(accountService.list(jwt.getSubject()));
    }

    @GetMapping("/{publicId}")
    public ResponseEntity<AccountResponse> get(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID publicId) {

        AccountResponse body = accountService.get(jwt.getSubject(), publicId);
        return ResponseEntity.ok().eTag(etag(body.version())).body(body);
    }

    @PatchMapping("/{publicId}")
    public ResponseEntity<AccountResponse> update(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID publicId,
            @RequestHeader(value = HttpHeaders.IF_MATCH, required = false) String ifMatch,
            @Valid @RequestBody UpdateAccountRequest request) {

        AccountResponse body = accountService.update(jwt.getSubject(), publicId, ifMatch, request);
        return ResponseEntity.ok().eTag(etag(body.version())).body(body);
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<Void> close(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID publicId,
            @RequestHeader(value = HttpHeaders.IF_MATCH, required = false) String ifMatch) {

        accountService.close(jwt.getSubject(), publicId, ifMatch);
        return ResponseEntity.noContent().build();
    }

    private static String etag(long version) {
        return "\"" + version + "\"";
    }
}