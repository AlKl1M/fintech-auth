package com.alkl1m.auth.util;

import com.alkl1m.auth.domain.entity.User;
import com.alkl1m.auth.service.impl.UserDetailsImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Утилита для работы с JSON Web Tokens.
 * Этот класс отвечает за создание, валидацию и извлечение информации из JWT.
 *
 * @author alkl1m
 */
@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${application.security.jwt.secret}")
    private String jwtSecret;

    @Value("${application.security.jwt.expirationMs}")
    private int jwtExpirationMs;

    @Value("${application.security.jwt.cookieName}")
    private String jwtCookie;

    @Value("${application.security.jwt.refreshCookieName}")
    private String jwtRefreshCookie;

    /**
     * Генерирует JWT cookie на основе информации о пользователе.
     *
     * @param userPrincipal объект, содержащий информацию о пользователе
     * @return ResponseCookie с JWT
     */
    public ResponseCookie generateJwtCookie(UserDetailsImpl userPrincipal) {
        String jwt = generateTokenFromUserDetails(userPrincipal);
        return generateCookie(jwtCookie, jwt);
    }

    /**
     * Генерирует JWT cookie на основе объекта пользователя.
     *
     * @param user объект пользователя
     * @return ResponseCookie с JWT
     */
    public ResponseCookie generateJwtCookie(User user) {
        String jwt = generateTokenFromUser(user);
        return generateCookie(jwtCookie, jwt);
    }

    /**
     * Генерирует RefreshToken cookie на основе переданного refreshToken.
     *
     * @param refreshToken токен обновления
     * @return ResponseCookie с токеном обновления
     */
    public ResponseCookie generateRefreshJwtCookie(String refreshToken) {
        return generateCookie(jwtRefreshCookie, refreshToken);
    }

    /**
     * Извлекает JWT из cookies запроса.
     *
     * @param request HTTP-запрос
     * @return JWT в виде строки
     */
    public String getJwtFromCookies(HttpServletRequest request) {
        return getCookieValueByName(request, jwtCookie);
    }


    /**
     * Извлекает refresh token из cookies запроса.
     *
     * @param request HTTP-запрос
     * @return токен обновления в виде строки
     */
    public String getJwtRefreshFromCookies(HttpServletRequest request) {
        return getCookieValueByName(request, jwtRefreshCookie);
    }

    /**
     * Создает пустое JWT cookie для очистки.
     *
     * @return ResponseCookie с пустым значением
     */
    public ResponseCookie getCleanJwtCookie() {
        return ResponseCookie.from(jwtCookie, "").path("/").build();
    }

    /**
     * Создает пустое JWT cookie для очистки refresh token.
     *
     * @return ResponseCookie с пустым значением для токена обновления
     */
    public ResponseCookie getCleanJwtRefreshToken() {
        return ResponseCookie.from(jwtRefreshCookie, "").path("/").build();
    }

    /**
     * Извлекает логин из JWT токена.
     *
     * @param token JWT токен
     * @return логин пользователя
     */
    public String getLoginFromJwtToken(String token) {
        return Jwts.parser().verifyWith(key()).build()
                .parseSignedClaims(token).getPayload().getSubject();
    }

    /**
     * Генерирует секретный ключ на основе конфигурации.
     *
     * @return SecretKey для подписи JWT
     */
    private SecretKey key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    /**
     * Проверяет валидность JWT токена.
     *
     * @param authToken JWT токен для проверки
     * @return true, если токен валиден; false в противном случае
     */
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().verifyWith(key()).build().parseSignedClaims(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

    /**
     * Генерирует JWT токен на основе объекта пользователя.
     *
     * @param user объект пользователя
     * @return сгенерированный JWT токен в виде строки
     */
    public String generateTokenFromUser(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getRoles().stream()
                .map(role -> role.getName().name())
                .toList());

        return Jwts
                .builder()
                .claims(claims)
                .subject(user.getLogin())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key(), Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Генерирует JWT токен на основе объекта UserDetailsImpl.
     *
     * @param userDetails объект UserDetailsImpl
     * @return сгенерированный JWT токен в виде строки
     */
    public String generateTokenFromUserDetails(UserDetailsImpl userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList());

        return Jwts
                .builder()
                .claims(claims)
                .subject(userDetails.getLogin())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key(), Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Создает cookie с заданным именем и значением.
     *
     * @param name  имя cookie
     * @param value значение cookie
     * @return ResponseCookie с заданными параметрами
     */
    private ResponseCookie generateCookie(String name, String value) {
        return ResponseCookie.from(name, value)
                .path("/")
                .maxAge(24L * 60 * 60)
                .httpOnly(true)
                .build();
    }

    /**
     * Получает значение cookie по его имени из запроса.
     *
     * @param request объект HttpServletRequest, содержащий информацию о запросе
     * @param name    имя cookie, значение которого нужно получить
     * @return значение cookie, если оно существует; null, если cookie с указанным именем не найден
     */
    private String getCookieValueByName(HttpServletRequest request, String name) {
        Cookie cookie = WebUtils.getCookie(request, name);
        if (cookie != null) {
            return cookie.getValue();
        } else {
            return null;
        }
    }
}