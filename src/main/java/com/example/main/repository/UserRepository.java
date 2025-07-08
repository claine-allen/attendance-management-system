package com.example.main.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.main.model.User;

import java.util.Optional;

/**
 * Repository interface for User entities.
 * Provides standard CRUD operations and custom query methods for User data.
 */
@Repository // Marks this interface as a Spring Data JPA repository
public interface UserRepository extends JpaRepository<User,Long> {

    /**
     * Finds a User by their email address.
     * Spring Data JPA automatically generates the query based on the method name.
     * @param email The email address of the user.
     * @return An Optional containing the User if found, or empty if not found.
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user exists with the given email address.
     * @param email The email address to check.
     * @return True if a user with the email exists, false otherwise.
     */
    boolean existsByEmail(String email);
}
