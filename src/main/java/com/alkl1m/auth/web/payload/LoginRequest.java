package com.alkl1m.auth.web.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginRequest(
        @NotNull(message = "{fintech.auth.errors.login_is_null}")
        @NotBlank(message = "{fintech.auth.errors.login_is_blank}")
        String login,
        @NotNull(message = "{fintech.auth.errors.password_is_null}")
        @NotBlank(message = "{fintech.auth.errors.password_is_blank}")
        String password
) {
}
