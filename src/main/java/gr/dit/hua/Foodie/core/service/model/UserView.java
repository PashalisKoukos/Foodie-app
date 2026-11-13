package gr.dit.hua.Foodie.core.service.model;

import gr.dit.hua.Foodie.core.model.Role;

public record UserView(
        Long id,
        String username,
        String email,
        String mobilePhoneNumber,
        Role role
) {}