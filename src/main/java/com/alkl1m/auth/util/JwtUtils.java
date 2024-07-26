package com.alkl1m.auth.util;

import com.alkl1m.auth.domain.entity.User;
import com.alkl1m.auth.service.impl.UserDetailsImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
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
import java.security.Key;
import java.security.SignatureException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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

    public ResponseCookie generateJwtCookie(UserDetailsImpl userPrincipal) {
        String jwt = generateTokenFromUserDetails(userPrincipal);
        return generateCookie(jwtCookie, jwt);
    }

    public ResponseCookie generateJwtCookie(User user) {
        String jwt = generateTokenFromUser(user);
        return generateCookie(jwtCookie, jwt);
    }

    public ResponseCookie generateRefreshJwtCookie(String refreshToken) {
        return generateCookie(jwtRefreshCookie, refreshToken);
    }

    public String getJwtFromCookies(HttpServletRequest request) {
        return getCookieValueByName(request, jwtCookie);
    }

    public String getJwtRefreshFromCookies(HttpServletRequest request) {
        return getCookieValueByName(request, jwtRefreshCookie);
    }

    public ResponseCookie getCleanJwtCookie() {
        return ResponseCookie.from(jwtCookie, "").path("/").build();
    }

    public ResponseCookie getCleanJwtRefreshToken() {
        return ResponseCookie.from(jwtRefreshCookie, "").path("/").build();
    }

    public String getLoginFromJwtToken(String token) {
        return Jwts.parser().verifyWith(key()).build()
                .parseSignedClaims(token).getPayload().getSubject();
    }

    private SecretKey key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

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

    private ResponseCookie generateCookie(String name, String value) {
        return ResponseCookie.from(name, value).path("/").maxAge(24 * 60 * 60).httpOnly(true).build();
    }

    private String getCookieValueByName(HttpServletRequest request, String name) {
        Cookie cookie = WebUtils.getCookie(request, name);
        if (cookie != null) {
            return cookie.getValue();
        } else {
            return null;
        }
    }
}