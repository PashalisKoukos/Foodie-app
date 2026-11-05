package gr.dit.hua.Foodie.core.repository;

import gr.dit.hua.Foodie.core.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Data access for User. Spring Data generates the implementation at startup,
// and the queries below are built from the method names.
public interface UserRepository extends JpaRepository<User, Long> {

    // Finds a user by email. Optional because email is unique (one result or none).
    // Used when logging in.
    Optional<User> findByEmail(String email);

    // Same as above, but by username.
    Optional<User> findByUsername(String username);

    // Finds a user by mobile phone number. Useful for login or user lookup.
    Optional<User> findByMobilePhoneNumber(String mobilePhoneNumber);

    // Checks if the email is already taken, without loading the whole user.
    // Used during registration.
    boolean existsByEmail(String email);

    // Checks if the username is already taken. Used during registration.
    boolean existsByUsername(String username);

    // Checks if the mobile phone number is already taken. Used during registration.
    boolean existsByMobilePhoneNumber(String mobilePhoneNumber);
}