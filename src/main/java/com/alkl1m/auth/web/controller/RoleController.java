package com.alkl1m.auth.web.controller;

import com.alkl1m.auth.service.RoleService;
import com.alkl1m.auth.web.payload.AddRolesPayload;
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
public class RoleController {
    private final RoleService roleService;

    /**
     * Сохраняет роли, переданные в теле запроса.
     *
     * @param payload объект, содержащий данные для добавления ролей
     * @return ResponseEntity с сообщением об успешном сохранении ролей
     */
    @PutMapping("/save")
    public ResponseEntity<String> saveRole(@RequestBody AddRolesPayload payload) {
        roleService.saveRole(payload);
        return ResponseEntity.ok("Роли успешно сохранены");
    }

}
