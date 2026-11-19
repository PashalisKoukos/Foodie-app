package gr.dit.hua.Foodie.core.security;

import gr.dit.hua.Foodie.core.model.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Immutable view implementing Spring's {@link UserDetails} for representing a user at runtime.
 */
@SuppressWarnings("RedundantMethodOverride")
public final class ApplicationUserDetails implements UserDetails {

    private final long userId;
    private final String username;
    private final String passwordHash;
    private final Role role;

    public ApplicationUserDetails(final long userId,
                                  final String username,
                                  final String passwordHash,
                                  final Role role) {
        if (userId <= 0) {
            throw new IllegalArgumentException();
        }
        if (username == null) {
            throw new NullPointerException();
        }
        if (username.isBlank()) {
            throw new IllegalArgumentException();
        }
        if (passwordHash == null) {
            throw new NullPointerException();
        }
        if (passwordHash.isBlank()) {
            throw new IllegalArgumentException();
        }
        if (role == null) {
            throw new NullPointerException();
        }

        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public long userId() {
        return this.userId;
    }

    public Role role() {
        return this.role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        final String authority;
        if (this.role == Role.OWNER) {
            authority = "ROLE_OWNER";
        } else if (this.role == Role.CUSTOMER) {
            authority = "ROLE_CUSTOMER";
        } else {
            throw new RuntimeException("Invalid role: " + this.role);
        }
        return Collections.singletonList(new SimpleGrantedAuthority(authority));
    }

    @Override
    public String getPassword() {
        return this.passwordHash;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}