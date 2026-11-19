package gr.dit.hua.Foodie.core.security;

import gr.dit.hua.Foodie.core.model.User;
import gr.dit.hua.Foodie.core.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementation of Spring's {@code UserDetailsService} for providing application users.
 */
@Service
public class ApplicationUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public ApplicationUserDetailsService(final UserRepository userRepository) {
        if (userRepository == null) {
            throw new NullPointerException();
        }
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        if (username.isBlank()) {
            throw new IllegalArgumentException();
        }

        final String normalizedUsername = username.strip();
        final User user = this.userRepository
                .findByUsernameIgnoreCase(normalizedUsername)
                .orElse(null);

        if (user == null) {
            throw new UsernameNotFoundException("User with username " + normalizedUsername + " does not exist");
        }

        return new ApplicationUserDetails(
                user.getId(),
                user.getUsername(),
                user.getPasswordHash(),
                user.getRole()
        );
    }
}