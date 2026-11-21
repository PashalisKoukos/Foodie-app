package gr.dit.hua.Foodie.core.service.model;

public record RestaurantItemView(
        Long id,
        String name,
        String description,
        java.math.BigDecimal price,
        boolean available,
        Long restaurantId
) {
}