package com.alkl1m.auth.web.controller;

import com.alkl1m.auth.domain.entity.Role;
import com.alkl1m.auth.domain.entity.User;
import com.alkl1m.auth.domain.enums.ERole;
import com.alkl1m.auth.repository.RoleRepository;
import com.alkl1m.auth.repository.UserRepository;
import com.alkl1m.auth.web.payload.AddRolesPayload;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping("/roles")
public class RoleController {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @PutMapping("/save")
    public ResponseEntity<?> saveRole(@RequestBody AddRolesPayload payload,
                                      final HttpServletRequest request) {
        Optional<User> optionalUser = userRepository.findByLogin(payload.login());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Пользователь не найден");
        }

        User user = optionalUser.get();

        Set<Role> roles = new HashSet<>();
        for (ERole roleEnum : payload.roles()) {
            Optional<Role> optionalRole = roleRepository.findByName(roleEnum);
            if (optionalRole.isPresent()) {
                roles.add(optionalRole.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Роль " + roleEnum + " не найдена");
            }
        }

        user.setRoles(roles);

        userRepository.save(user);

        return ResponseEntity.ok("Роли успешно сохранены для пользователя " + user.getLogin());
    }

}
