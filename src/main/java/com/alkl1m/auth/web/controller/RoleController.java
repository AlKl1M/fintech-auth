package com.alkl1m.auth.web.controller;

import com.alkl1m.auth.service.RoleService;
import com.alkl1m.auth.web.payload.AddRolesPayload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для управления ролями в приложении.
 *
 * @author alkl1m
 */
@RestController
@AllArgsConstructor
@RequestMapping("/roles")
@Tag(name = "roles", description = "The Auth API")
public class RoleController {
    private final RoleService roleService;

    /**
     * Сохраняет роли, переданные в теле запроса.
     *
     * @param payload объект, содержащий данные для добавления ролей
     * @return ResponseEntity с сообщением об успешном сохранении ролей
     */
    @Operation(summary = "Добавление ролей пользователю", tags = "roles")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешно добавил роли пользователю")
    })
    @PutMapping("/save")
    public ResponseEntity<String> saveRole(@RequestBody AddRolesPayload payload) {
        roleService.saveRole(payload);
        return ResponseEntity.ok("Роли успешно сохранены");
    }

}
