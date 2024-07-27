package com.alkl1m.auth.repository;

import com.alkl1m.auth.TestBeans;
import com.alkl1m.auth.domain.entity.RefreshToken;
import com.alkl1m.auth.domain.entity.Role;
import com.alkl1m.auth.domain.entity.User;
import com.alkl1m.auth.domain.enums.ERole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@DataJpaTest
@Import(TestBeans.class)
@Sql("/sql/user.sql")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RefreshTokenRepositoryTest {

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TestEntityManager entityManager;

    private RefreshToken newRefreshToken;
    private User user;

    @BeforeEach
    void setUp() {
        user = userRepository.findByLogin("John Doe").get();
        newRefreshToken = new RefreshToken(1L, user, "6aa49bc8-b0d1-4c61-859f-0bec00c4a88a", Instant.now().plusMillis(864000));
    }

    @Test
    void testSave_withValidPayload_returnsSameToken() {
        RefreshToken token = refreshTokenRepository.save(newRefreshToken);
        assertEquals(token.getToken(), newRefreshToken.getToken());
        assertEquals(token.getExpiryDate(), newRefreshToken.getExpiryDate());
    }

    @Test
    void testUpdate_withValidPayload_returnsSameToken() {
        String newToken = "397b4759-568c-451d-83dc-a5d6c372b7fd";
        newRefreshToken.setToken(newToken);
        refreshTokenRepository.save(newRefreshToken);
        assertThat(entityManager.find(RefreshToken.class, newRefreshToken.getId()).getToken()).isEqualTo(newToken);
    }

    @Test
    void testFindByToken_withValidPayload_returnsValidToken() {
        Optional<RefreshToken> retrievedRole = refreshTokenRepository.findByToken(newRefreshToken.getToken());
        assertNotNull(retrievedRole);
    }

    @Test
    void testDeleteByUser_withValidPayload_returnsNoTokenInDb() {
        refreshTokenRepository.deleteByUser(user);
        assertThat(entityManager.find(RefreshToken.class, newRefreshToken.getId())).isNull();
    }


}