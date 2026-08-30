package com.nomevini.opengrana.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private JwtEncoder jwtEncoder;

    private JwtService jwtService;

    @Test
    void shouldGenerateTokenWithCorrectClaims() {
        jwtService = new JwtService(jwtEncoder);
        ReflectionTestUtils.setField(jwtService, "expirationMinutes", 60L);

        Jwt fakeJwt = Jwt.withTokenValue("fake-token-value")
                .header("alg", "HS256")
                .claim("sub", "test@email.com")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(fakeJwt);

        String token = jwtService.generateToken("test@email.com");

        assertThat(token).isEqualTo("fake-token-value");

        ArgumentCaptor<JwtEncoderParameters> captor = ArgumentCaptor.forClass(JwtEncoderParameters.class);
        verify(jwtEncoder).encode(captor.capture());

        JwtEncoderParameters capturedParams = captor.getValue();

        assertThat(capturedParams.getClaims().getSubject()).isEqualTo("test@email.com");
        assertThat(capturedParams.getClaims().getIssuer().toString()).isEqualTo("https://opengrana.app");
        assertThat(capturedParams.getJwsHeader().getAlgorithm().getName()).isEqualTo("HS256");
    }

    @Test
    void shouldSetExpirationBasedOnConfiguredMinutes() {
        jwtService = new JwtService(jwtEncoder);
        ReflectionTestUtils.setField(jwtService, "expirationMinutes", 30L);

        Jwt fakeJwt = Jwt.withTokenValue("fake-token-value")
                .header("alg", "HS256")
                .claim("sub", "test@email.com")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(1800))
                .build();

        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(fakeJwt);

        jwtService.generateToken("test@email.com");

        ArgumentCaptor<JwtEncoderParameters> captor = ArgumentCaptor.forClass(JwtEncoderParameters.class);
        verify(jwtEncoder).encode(captor.capture());

        Instant issuedAt = captor.getValue().getClaims().getIssuedAt();
        Instant expiresAt = captor.getValue().getClaims().getExpiresAt();

        long minutesBetween = Duration.between(issuedAt, expiresAt).toMinutes();
        assertThat(minutesBetween).isEqualTo(30L);
    }
}