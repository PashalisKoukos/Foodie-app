package gr.dit.hua.Foodie.core.security;

import gr.dit.hua.Foodie.core.model.Role;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Component for providing the current authenticated user.
 */
@Component
public final class CurrentUserProvider {

    public Optional<CurrentUser> getCurrentUser() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return Optional.empty();
        }
        if (authentication.getPrincipal() instanceof ApplicationUserDetails userDetails) {
            return Optional.of(new CurrentUser(
                    userDetails.userId(),
                    userDetails.getUsername(),
                    userDetails.role()
            ));
        }
        return Optional.empty();
    }

    public CurrentUser requireCurrentUser() {
        return this.getCurrentUser().orElseThrow(() -> new SecurityException("not authenticated"));
    }

    public long requiredOwnerId() {
        final var currentUser = this.requireCurrentUser();
        if (currentUser.role() != Role.OWNER) throw new SecurityException("Student type/role required");
        return currentUser.userId();
    }
}