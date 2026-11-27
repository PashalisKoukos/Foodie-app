package gr.dit.hua.Foodie.web.rest.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ClientTokenRequest(
        @NotNull @NotBlank String clientId,
        @NotNull @NotBlank String clientSecret
) {}
