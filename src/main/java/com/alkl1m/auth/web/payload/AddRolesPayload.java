package com.alkl1m.auth.web.payload;

import com.alkl1m.auth.domain.enums.ERole;

import java.util.Set;

/**
 * Payload для добавления ролей юзеру по логину.
 *
 * @param login логин юзера.
 * @param roles список ролей для добавления.
 * @author alkl1m
 */
public record AddRolesPayload(
        String login,
        Set<ERole> roles
) {
}
