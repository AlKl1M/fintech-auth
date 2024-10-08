package com.alkl1m.auth.web.controller;

import com.alkl1m.auth.domain.entity.RefreshToken;
import com.alkl1m.auth.domain.exception.TokenRefreshException;
import com.alkl1m.auth.service.UserService;
import com.alkl1m.auth.service.impl.RefreshTokenServiceImpl;
import com.alkl1m.auth.service.impl.UserDetailsImpl;
import com.alkl1m.auth.util.JwtUtils;
import com.alkl1m.auth.web.payload.LoginRequest;
import com.alkl1m.auth.web.payload.SignupRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для обработки аутентификации пользователей.
 * Регистрация, вход в систему, обновление токена и выход из системы.
 *
 * @author alkl1m
 */
@RestController
@AllArgsConstructor
@RequestMapping("/auth")
@Tag(name = "auth", description = "The Auth API")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final PasswordEncoder encoder;
    private final RefreshTokenServiceImpl refreshTokenService;
    private final JwtUtils jwtUtils;

    /**
     * Регистрация нового пользователя.
     *
     * @param signupRequest объект, содержащий данные для регистрации пользователя
     * @return ResponseEntity с кодом состояния 200 (OK) при успешной регистрации
     */
    @Operation(summary = "Регистрация нового пользователя", tags = "auth")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешно зарегистрировал нового пользователя")
    })
    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@Valid @RequestBody SignupRequest signupRequest) {

        userService.save(signupRequest.login(),
                signupRequest.email(),
                encoder.encode(signupRequest.password()));

        return ResponseEntity.ok().build();
    }

    /**
     * Аутентификация пользователя и получение JWT токенов.
     *
     * @param loginRequest объект, содержащий данные для входа пользователя
     * @return ResponseEntity с заголовками, содержащими JWT и токен обновления,
     * а также сообщением об успешном входе
     */
    @Operation(summary = "Аутентификация пользователя и получение JWT токенов", tags = "auth")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Пользователь успешно аутентифицирован")
    })
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.login(), loginRequest.password()));
        SecurityContextHolder.getContext().setAuthentication(authentication);


        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        ResponseCookie jwtRefreshCookie = jwtUtils.generateRefreshJwtCookie(refreshToken.getToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
                .body("User logged in successfully!");
    }

    /**
     * Обновление JWT токена на основе refreshToken.
     *
     * @param request HTTP-запрос, содержащий токен обновления в cookies
     * @return ResponseEntity с заголовком, содержащим новый JWT и сообщением об успешном обновлении токена
     */
    @Operation(summary = "Обновление JWT токена на основе refreshToken", tags = "auth")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "JWT токен успешно обновлен")
    })
    @PostMapping("/refreshToken")
    public ResponseEntity<String> refreshToken(HttpServletRequest request) {
        String refreshToken = jwtUtils.getJwtRefreshFromCookies(request);
        if ((refreshToken != null) && (!refreshToken.isEmpty())) {
            return refreshTokenService.findByToken(refreshToken)
                    .map(refreshTokenService::verifyExpiration)
                    .map(RefreshToken::getUser)
                    .map(user -> {
                        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(user);
                        return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                                .body("Token is refreshed successfully!");
                    })
                    .orElseThrow(() -> new TokenRefreshException(refreshToken,
                            "Refresh token is not id database!"));
        }
        return ResponseEntity.badRequest().body("Refresh Token is empty!");
    }

    /**
     * Выход пользователя из системы и удаление токенов.
     *
     * @return ResponseEntity с заголовками, содержащими очищенные JWT и токен обновления,
     * а также сообщением о выходе из системы
     */
    @Operation(summary = "Выход пользователя из системы и удаление токенов", tags = "auth")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Пользователь успешно вышел из системы")
    })
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetailsImpl userDetails) {
                Long userId = userDetails.getId();
                refreshTokenService.deleteByUserId(userId);
            }
        }
        ResponseCookie jwtCookie = jwtUtils.getCleanJwtCookie();
        ResponseCookie jwtRefreshCookie = jwtUtils.getCleanJwtRefreshToken();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
                .body("You've been signed out!");
    }
}
