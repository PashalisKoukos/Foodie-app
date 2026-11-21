package gr.dit.hua.Foodie.core.service.model;

public record AddressView(
        Long id,
        String label,
        String street,
        String city,
        String postalCode
) {
}