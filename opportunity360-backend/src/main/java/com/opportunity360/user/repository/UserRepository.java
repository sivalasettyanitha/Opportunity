
package com.opportunity360.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.opportunity360.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
