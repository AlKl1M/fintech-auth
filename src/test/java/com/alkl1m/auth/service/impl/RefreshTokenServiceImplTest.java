package com.alkl1m.auth.service.impl;

import com.alkl1m.auth.domain.entity.RefreshToken;
import com.alkl1m.auth.domain.entity.User;
import com.alkl1m.auth.domain.exception.TokenRefreshException;
import com.alkl1m.auth.repository.RefreshTokenRepository;
import com.alkl1m.auth.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceImplTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RefreshTokenServiceImpl refreshTokenService;

    private User user;
    private RefreshToken refreshToken;
    private RefreshToken expiredToken;

    @BeforeEach
    public void setup() {
        refreshTokenService.setRefreshTokenDurationMs(3600000L);
        user = new User(1L,"login", "email@example.com", "password", new HashSet<>());
        refreshToken = new RefreshToken(1L, user, UUID.randomUUID().toString(), Instant.now().plusMillis(refreshTokenService.getRefreshTokenDurationMs()));
        expiredToken = new RefreshToken(2L, user, UUID.randomUUID().toString(), Instant.now().minusMillis(1000));
    }

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void testCreateRefreshToken_withUserExistsAndRefreshTokenIsValid_returnNewToken() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);
        RefreshToken result = refreshTokenService.createRefreshToken(user.getId());
        assertEquals(user, result.getUser());
        assertNotNull(result.getToken());
    }

    @Test
    void testVerifyExpiration_withExpiredTokenThrowsTokenRefreshException_verifyExpiration() {
        assertThrows(TokenRefreshException.class, () -> {
            refreshTokenService.verifyExpiration(expiredToken);
        });
        verify(refreshTokenRepository, times(1)).delete(expiredToken);
    }

    @Test
    void testVerifyExpiration_withValidToken_ReturnsToken() {
        RefreshToken result = refreshTokenService.verifyExpiration(refreshToken);
        assertEquals(refreshToken, result);
        verify(refreshTokenRepository, never()).delete(refreshToken);
    }

}