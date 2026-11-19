package gr.dit.hua.Foodie.core.security;

import gr.dit.hua.Foodie.core.model.Role;

public record CurrentUser(
        Long userId,
        String username,
        Role role
) {}