package gr.dit.hua.Foodie.web.rest.model;
/**
 * @see ClientAuthResource
 */
public record ClientTokenResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {}
