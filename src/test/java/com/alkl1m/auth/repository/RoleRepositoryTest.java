package com.alkl1m.auth.repository;

import com.alkl1m.auth.TestBeans;
import com.alkl1m.auth.domain.entity.Role;
import com.alkl1m.auth.domain.enums.ERole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@DataJpaTest
@Import(TestBeans.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RoleRepositoryTest {

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    TestEntityManager entityManager;

    private Role newRole;

    @BeforeEach
    void setUp() {
        newRole = Role.builder().name(ERole.USER).build();
    }

    @Test
    void testSave_withValidPayload_returnsSameRole() {
        Role role = roleRepository.save(newRole);
        assertEquals(role.getName(), newRole.getName());
    }

    @Test
    void testUpdate_withValidPayload_returnsSameRole() {
        ERole newName = ERole.ADMIN;
        newRole.setName(newName);
        roleRepository.save(newRole);
        assertThat(entityManager.find(Role.class, newRole.getId()).getName()).isEqualTo(newName);
    }

    @Test
    void testFindById_withValidPayload_returnsValidRole() {
        entityManager.persist(newRole);
        Optional<Role> retrievedRole = roleRepository.findById(newRole.getId());
        assertThat(retrievedRole).contains(newRole);
    }

    @Test
    void testFindByName_withValidPayload_returnsValidRole() {
        Optional<Role> retrievedRole = roleRepository.findByName(ERole.USER);
        assertNotNull(retrievedRole);
    }

    @Test
    void testDelete_withValidPayload_returnsNoRoleInDb() {
        entityManager.persist(newRole);
        roleRepository.delete(new Role(1L, ERole.USER));
        assertThat(entityManager.find(Role.class, 1L)).isNull();
    }

}