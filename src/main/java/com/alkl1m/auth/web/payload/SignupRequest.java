package com.alkl1m.auth.web.payload;

public record SignupRequest(
        String name,
        String email,
        String password
) {
}
