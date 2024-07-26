package com.alkl1m.auth.web.payload;

import com.alkl1m.auth.domain.enums.ERole;

import java.util.Set;

public record AddRolesPayload(
        String login,
        Set<ERole> roles
) {
}
