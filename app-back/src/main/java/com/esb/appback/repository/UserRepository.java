package com.esb.appback.repository;

import com.esb.appback.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    // You can add more when needed, examples:
    // Optional<User> findByName(String name);
    // List<User> findByRole(String role);
    // List<User> findByRoleOrderByNameAsc(String role);
}
