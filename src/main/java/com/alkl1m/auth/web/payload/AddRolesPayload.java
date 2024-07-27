package com.alkl1m.auth.web.payload;

import com.alkl1m.auth.domain.enums.ERole;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

/**
 * Payload для добавления ролей юзеру по логину.
 *
 * @param login логин юзера.
 * @param roles список ролей для добавления.
 * @author alkl1m
 */
public record AddRolesPayload(
        @Schema(description = "Логин пользователя, которому добавляем роли")
        String login,
        @Schema(description = "Список ролей для добавления пользователю")
        Set<ERole> roles
) {
}
