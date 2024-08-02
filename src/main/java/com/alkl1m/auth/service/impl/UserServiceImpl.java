package com.alkl1m.auth.service.impl;

import com.alkl1m.auth.domain.entity.User;
import com.alkl1m.auth.domain.enums.ERole;
import com.alkl1m.auth.domain.exception.UserAlreadyExistsException;
import com.alkl1m.auth.repository.RoleRepository;
import com.alkl1m.auth.repository.UserRepository;
import com.alkl1m.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Реализация UserService, сохраняющая пользователя и проверяющая его существование.
 *
 * @author alkl1m
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    /**
     * Сохранение нового пользователя, если его не существует.
     *
     * @param login    логин нового юзера.
     * @param email    почта нового юзера.
     * @param password пароль нового юзера.
     */
    @Override
    public void save(String login, String email, String password) {
        checkUserExists(login, email);

        User user = new User(
                login,
                email,
                password,
                Set.of(roleRepository.findByName(ERole.USER).orElseThrow())
        );
        userRepository.save(user);
    }

    private void checkUserExists(String login, String email) {
        if (userRepository.existsByLogin(login) || userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("Пользователь уже зарегистрирован");
        }
    }
}
