package com.alkl1m.auth.repository;

import com.alkl1m.auth.TestBeans;
import com.alkl1m.auth.domain.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@DataJpaTest
@Import(TestBeans.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    TestEntityManager entityManager;

    private User newUser;

    @BeforeEach
    void setUp() {
        newUser = new User("login", "email@example.com", "password", new HashSet<>());
    }

    @Test
    void testSave_withValidPayload_returnsSameUser() {
        User user = userRepository.save(newUser);
        assertEquals(user.getLogin(), newUser.getLogin());
        assertEquals(user.getEmail(), newUser.getEmail());
    }

    @Test
    void testUpdate_withValidPayload_returnsSameUser() {
        String newEmail = "newemail@example.com";
        newUser.setEmail(newEmail);
        userRepository.save(newUser);
        assertThat(entityManager.find(User.class, newUser.getId()).getEmail()).isEqualTo(newEmail);
    }

    @Test
    void testFindById_withValidPayload_returnsValidUser() {
        entityManager.persist(newUser);
        Optional<User> retrievedUser = userRepository.findById(newUser.getId());
        assertThat(retrievedUser).contains(newUser);
    }

    @Test
    void testFindByLogin_withValidPayload_returnsValidUser() {
        entityManager.persist(newUser);
        Optional<User> retrievedUser = userRepository.findByLogin("login");
        assertThat(retrievedUser).contains(newUser);
    }

    @Test
    void testExistsByLogin_withValidPayload_returnsValidData() {
        entityManager.persist(newUser);

        Assertions.assertTrue(userRepository.existsByLogin("login"));
    }

    @Test
    void testExistsByEmail_withValidPayload_returnsValidData() {
        entityManager.persist(newUser);

        Assertions.assertTrue(userRepository.existsByEmail("email@example.com"));
    }

    @Test
    void testDelete_withValidPayload_returnsNoUserInDb() {
        entityManager.persist(newUser);
        userRepository.delete(newUser);
        assertThat(entityManager.find(User.class, newUser.getId())).isNull();
    }

}