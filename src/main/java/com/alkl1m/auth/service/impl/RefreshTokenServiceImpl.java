package com.alkl1m.auth.service.impl;

import com.alkl1m.auth.domain.entity.RefreshToken;
import com.alkl1m.auth.domain.entity.User;
import com.alkl1m.auth.domain.exception.TokenRefreshException;
import com.alkl1m.auth.repository.RefreshTokenRepository;
import com.alkl1m.auth.repository.UserRepository;
import com.alkl1m.auth.service.RefreshTokenService;
import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Реализация RefreshTokenService для управления refresh токенами.
 *
 * @author alkl1m
 */
@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Setter
    @Getter
    @Value("${application.security.jwt.refreshExpirationMs}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    /**
     * Находит refresh токен по его строковому значению.
     *
     * @param token строковое значение refresh токена.
     * @return объект Optional, содержащий найденный токен, или пустой, если токен не найден.
     */
    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    /**
     * Создает новый refresh токен для указанного пользователя.
     *
     * @param userId идентификатор пользователя, для которого создается токен.
     * @return созданный объект RefreshToken.
     */
    @Override
    public RefreshToken createRefreshToken(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User not found with ID: %s", userId)));

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());

        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Проверка срока действия refresh токена.
     *
     * @param token объект RefreshToken для проверки.
     * @return объект RefreshToken, если срок действия не истек.
     */
    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired");
        }
        return token;
    }

    /**
     * Удаляет все refresh токены, связанные с указанным пользователем.
     *
     * @param userId идентификатор пользователя, чьи токены будут удалены.
     */
    @Override
    @Transactional
    public void deleteByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
        refreshTokenRepository.deleteByUser(user);
    }
}