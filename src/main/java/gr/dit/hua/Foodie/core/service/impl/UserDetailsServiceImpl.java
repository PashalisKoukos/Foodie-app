
package gr.dit.hua.Foodie.core.service.impl;

import gr.dit.hua.Foodie.core.model.User;
import gr.dit.hua.Foodie.core.repository.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


// It provides custom user-loading functionality for Spring Security.
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    // Repository used to retrieve user information from the database.
    private final UserRepository userRepository;

    // Constructor injection of UserRepository.
    // Spring automatically provides the required repository instance.
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Loads a user's authentication details using their username.
    // Spring Security calls this method during the authentication process.
    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        // Searches the database for a user with the given username.
        // If no user is found, a UsernameNotFoundException is thrown.
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found: " + username
                        )
                );

        // Converts the application's User entity into a Spring Security UserDetails object.
        // The username and stored password hash are provided for authentication.
        // The user's role is also assigned as a Spring Security authority.
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPasswordHash())
                .roles(user.getRole().name())
                .build();
    }
}
