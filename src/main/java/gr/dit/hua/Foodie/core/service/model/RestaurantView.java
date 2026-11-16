package gr.dit.hua.Foodie.core.service.model;

public record RestaurantView(
        Long id,
        String name,
        String cuisine,
        String region,
        String address,
        boolean isOpen,
        double minimumOrderAmount,
        String ownerUsername
) {
}
