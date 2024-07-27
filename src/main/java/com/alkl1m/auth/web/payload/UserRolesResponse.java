package com.alkl1m.auth.web.payload;

import com.alkl1m.auth.domain.entity.Role;

import java.util.Set;

public record UserRolesResponse(
        Set<Role> roles
) {
}
