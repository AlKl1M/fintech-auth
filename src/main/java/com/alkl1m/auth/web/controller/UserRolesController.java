package com.alkl1m.auth.web.controller;

import com.alkl1m.auth.domain.entity.Role;
import com.alkl1m.auth.service.RoleService;
import com.alkl1m.auth.web.payload.UserRolesResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Set;

/**
 * Контроллер для управления ролями пользователей в приложении.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/user-roles")
@Tag(name = "user-roles", description = "The Auth API")
public class UserRolesController {
    private final RoleService roleService;

    /**
     * Получает роли для указанного пользователя.
     *
     * @param login     логин пользователя, для которого нужно получить роли
     * @param principal объект, представляющий текущего аутентифицированного пользователя
     * @return ResponseEntity с набором ролей для указанного пользователя
     */
    @Operation(summary = "Получение ролей для конкретного пользователя", tags = "user-roles")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешно получил роли пользователя",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = UserRolesResponse.class)))
                    })
    })
    @GetMapping("/{login}")
    public ResponseEntity<UserRolesResponse> getRoles(@PathVariable String login,
                                                      Principal principal) {
        String currentUserLogin = principal.getName();
        return ResponseEntity.ok(roleService.getRolesForUser(login, currentUserLogin));
    }

}
