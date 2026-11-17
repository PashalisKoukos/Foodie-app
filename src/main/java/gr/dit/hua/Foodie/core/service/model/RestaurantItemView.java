package gr.dit.hua.Foodie.core.service.model;

public record RestaurantItemView(
        Long id,
        String name,
        String description,
        double price,
        boolean available,
        Long restaurantId
) {
}