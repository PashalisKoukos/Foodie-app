package gr.dit.hua.Foodie.web.rest.model;

public record ClientTokenResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {}
