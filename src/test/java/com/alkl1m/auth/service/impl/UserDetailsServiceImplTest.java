package com.alkl1m.auth.service.impl;

import com.alkl1m.auth.domain.entity.User;
import com.alkl1m.auth.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("login", "email@example.com", "password", new HashSet<>());
    }

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    public void testLoadUserByLogin_withValidUser_returnsUser() {
        String login = "login";
        when(userRepository.findByLogin(login)).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername(login);

        assertEquals(userDetails.getUsername(), user.getLogin());
        assertEquals(userDetails.getPassword(), user.getPassword());
    }

}