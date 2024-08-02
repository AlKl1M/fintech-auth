package com.alkl1m.auth.web.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request для входа пользователя.
 *
 * @param login    логин пользователя
 * @param password пароль пользователя
 * @author alkl1m
 */
public record LoginRequest(
        @NotNull(message = "{fintech.auth.errors.login_is_null}")
        @NotBlank(message = "{fintech.auth.errors.login_is_blank}")
        @Schema(description = "Логин пользователя")
        String login,
        @NotNull(message = "{fintech.auth.errors.password_is_null}")
        @NotBlank(message = "{fintech.auth.errors.password_is_blank}")
        @Schema(description = "Пароль пользователя")
        String password
) {
}
