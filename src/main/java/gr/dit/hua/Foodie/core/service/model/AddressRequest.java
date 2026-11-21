package gr.dit.hua.Foodie.core.service.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// Data sent by the customer to add an address (input validation, Β2γ)
public record AddressRequest(
        @NotBlank(message = "Label is required")
        @Size(max = 50, message = "Label can have at most 50 characters")
        String label,

        @NotBlank(message = "Street is required")
        @Size(max = 100, message = "Street can have at most 100 characters")
        String street,

        @NotBlank(message = "City is required")
        @Size(max = 50, message = "City can have at most 50 characters")
        String city,

        @NotBlank(message = "Postal code is required")
        @Pattern(regexp = "^\\d{5}$", message = "Postal code must have 5 digits")
        String postalCode
) {
}