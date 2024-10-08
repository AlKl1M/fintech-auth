package com.alkl1m.auth.repository;

import com.alkl1m.auth.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author alkl1m
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLogin (String login);

    Boolean existsByLogin(String login);

    Boolean existsByEmail(String email);

}
