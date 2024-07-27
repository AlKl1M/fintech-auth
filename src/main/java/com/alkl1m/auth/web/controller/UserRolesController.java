package com.alkl1m.auth.web.controller;

import com.alkl1m.auth.domain.entity.Role;
import com.alkl1m.auth.service.RoleService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping("/user-roles")
public class UserRolesController {
    private final RoleService roleService;

    @GetMapping("/{login}")
    public ResponseEntity<Set<Role>> getRoles(@PathVariable String login,
                                              Principal principal) {
        String currentUserLogin = principal.getName();
        return ResponseEntity.ok(roleService.getRolesForUser(login, currentUserLogin));
    }

}
