package gr.dit.hua.Foodie.core.security;

import java.util.Set;

public record ClientDetails(
        String id,
        String secret,
        Set<String> roles
) {}
