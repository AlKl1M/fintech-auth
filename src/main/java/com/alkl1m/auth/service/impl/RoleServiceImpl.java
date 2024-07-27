package com.alkl1m.auth.service.impl;

import com.alkl1m.auth.domain.entity.Role;
import com.alkl1m.auth.domain.entity.User;
import com.alkl1m.auth.domain.enums.ERole;
import com.alkl1m.auth.repository.RoleRepository;
import com.alkl1m.auth.repository.UserRepository;
import com.alkl1m.auth.service.RoleService;
import com.alkl1m.auth.web.payload.AddRolesPayload;
import com.alkl1m.auth.web.payload.UserRolesResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Реализация логики управления ролями пользователей.
 *
 * @author alkl1m
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    /**
     * Сохранение ролей для указанного пользователя.
     *
     * @param payload объект, содержащий информацию о пользователе и ролях.
     */
    @Override
    public void saveRole(AddRolesPayload payload) {
        Optional<User> optionalUser = userRepository.findByLogin(payload.login());
        if (optionalUser.isEmpty()) {
            throw new EntityNotFoundException("Пользователь не найден");
        }

        User user = optionalUser.get();

        Set<Role> roles = new HashSet<>();
        for (ERole roleEnum : payload.roles()) {
            Optional<Role> optionalRole = roleRepository.findByName(roleEnum);
            if (optionalRole.isPresent()) {
                roles.add(optionalRole.get());
            } else {
                throw new EntityNotFoundException("Роль не найдена");
            }
        }

        user.setRoles(roles);

        userRepository.save(user);
    }

    /**
     * Получение роли для указанного пользователя, проверка права доступа текущего пользователя.
     *
     * @param login            логин пользователя, для которого запрашиваются роли.
     * @param currentUserLogin логин текущего пользователя, выполняющего запрос.
     * @return набор ролей для указанного пользователя.
     */
    @Override
    public UserRolesResponse getRolesForUser(String login, String currentUserLogin) {
        Optional<User> userOptional = userRepository.findByLogin(currentUserLogin);
        if (userOptional.isEmpty()) {
            throw new EntityNotFoundException("Текущий пользователь не найден");
        }

        User currentUser = userOptional.get();
        Set<Role> currentUserRoles = currentUser.getRoles();

        boolean isAdmin = currentUserRoles.stream()
                .anyMatch(role -> role.getName() == ERole.ADMIN);

        if (!isAdmin && !currentUserLogin.equals(login)) {
            throw new AccessDeniedException("Недостаточно прав для доступа к ролям другого пользователя");
        }

        Optional<User> userToFetchOptional = userRepository.findByLogin(login);
        if (userToFetchOptional.isEmpty()) {
            throw new EntityNotFoundException("Пользователь не найден");
        }

        User userToFetch = userToFetchOptional.get();

        return new UserRolesResponse(userToFetch.getRoles());
    }

}
