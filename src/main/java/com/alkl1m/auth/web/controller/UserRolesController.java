package com.alkl1m.auth.web.controller;

import com.alkl1m.auth.domain.entity.Role;
import com.alkl1m.auth.domain.entity.User;
import com.alkl1m.auth.domain.enums.ERole;
import com.alkl1m.auth.repository.RoleRepository;
import com.alkl1m.auth.repository.UserRepository;
import com.alkl1m.auth.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Optional;
import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping("/user-roles")
public class UserRolesController {
    private final UserRepository userRepository;

    @GetMapping("/{login}")
    public ResponseEntity<?> saveRole(@PathVariable String login,
                                      final HttpServletRequest request,
                                      Principal principal) {
        String currentUserLogin = principal.getName();

        Optional<User> userOptional = userRepository.findByLogin(currentUserLogin);
        if (!userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Текущий пользователь не найден");
        }

        User currentUser = userOptional.get();
        Set<Role> currentUserRoles = currentUser.getRoles();

        boolean isAdmin = currentUserRoles.stream()
                .anyMatch(role -> role.getName() == ERole.ADMIN);

        if (!isAdmin && !currentUserLogin.equals(login)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Недостаточно прав для доступа к ролям другого пользователя");
        }

        Optional<User> userToFetchOptional = userRepository.findByLogin(login);
        if (userToFetchOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Пользователь не найден");
        }

        User userToFetch = userToFetchOptional.get();
        Set<Role> roles = userToFetch.getRoles();

        return ResponseEntity.ok(roles);
    }

}
