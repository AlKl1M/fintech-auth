package com.alkl1m.auth.web.payload;

public record LoginRequest(
        String login,
        String password
) {
}
