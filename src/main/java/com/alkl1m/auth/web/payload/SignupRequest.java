package com.alkl1m.auth.web.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request для регистрации пользователя.
 *
 * @param login    логин пользователя
 * @param password пароль пользователя
 * @author alkl1m
 */
public record SignupRequest(
        @NotNull(message = "{fintech.auth.errors.login_is_null}")
        @NotBlank(message = "{fintech.auth.errors.login_is_blank}")
        @Schema(description = "Логин пользователя")
        String login,
        @NotNull(message = "{fintech.auth.errors.email_is_null}")
        @NotBlank(message = "{fintech.auth.errors.email_is_blank}")
        @Schema(description = "Почта пользователя")
        String email,
        @NotNull(message = "{fintech.auth.errors.password_is_null}")
        @NotBlank(message = "{fintech.auth.errors.password_is_blank}")
        @Schema(description = "Пароль пользователя")
        String password
) {
}
