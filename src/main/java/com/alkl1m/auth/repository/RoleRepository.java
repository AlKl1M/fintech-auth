package com.alkl1m.auth.repository;

import com.alkl1m.auth.domain.entity.Role;
import com.alkl1m.auth.domain.enums.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(ERole name);

}
