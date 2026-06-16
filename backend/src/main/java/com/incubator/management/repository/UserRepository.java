package com.incubator.management.repository;

import com.incubator.management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * User Repository
 * ---------------
 * Provides database access for the User entity.
 * Extends JpaRepository which gives us standard CRUD operations for free:
 *   - save(), findById(), findAll(), deleteById(), count(), etc.
 *
 * Custom query methods below are auto-implemented by Spring Data JPA
 * based on the method name — no SQL needed.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find a user by their email address.
     * Used during login to look up the account.
     * Returns Optional — caller must handle the empty (not found) case.
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if an email address is already registered.
     * Used during registration to prevent duplicate accounts.
     */
    boolean existsByEmail(String email);

    /**
     * Find all users with a given role.
     * Used by admin to list all mentors (for assignment) or filter by role.
     */
    List<User> findByRole(User.Role role);

    /**
     * Find all users — used by admin manage-users page.
     */
    List<User> findAll();

    /**
     * Find all users with a given blocked status.
     * Used by admin to filter blocked users.
     */
    List<User> findByIsBlocked(boolean isBlocked);
}
