package com.alkl1m.auth.web.controller;

import com.alkl1m.auth.service.RoleService;
import com.alkl1m.auth.web.payload.AddRolesPayload;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/roles")
public class RoleController {
    private final RoleService roleService;

    @PutMapping("/save")
    public ResponseEntity<String> saveRole(@RequestBody AddRolesPayload payload) {
        roleService.saveRole(payload);
        return ResponseEntity.ok("Роли успешно сохранены");
    }

}
