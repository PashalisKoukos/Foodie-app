package gr.dit.hua.Foodie.web.rest;

import gr.dit.hua.Foodie.core.model.Restaurant;
import gr.dit.hua.Foodie.core.model.Role;
import gr.dit.hua.Foodie.core.model.User;
import gr.dit.hua.Foodie.core.repository.RestaurantRepository;
import gr.dit.hua.Foodie.core.repository.UserRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class DiagnosticsResource {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    public DiagnosticsResource(final UserRepository userRepository,
                               final RestaurantRepository restaurantRepository) {
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @GetMapping(value = "/diagnostics", produces = MediaType.TEXT_MARKDOWN_VALUE)
    public String test() {
        final StringBuilder sb = new StringBuilder();

        sb.append("# Diagnostics\n\n");

        final List<User> allUsers = this.userRepository.findAll();
        final List<String> customerLines = new ArrayList<>();
        final List<String> ownerLines = new ArrayList<>();

        for (User user : allUsers) {
            if (user.getRole() == Role.CUSTOMER) {
                customerLines.add(user.toString());
            } else if (user.getRole() == Role.OWNER) {
                ownerLines.add(user.toString());
            }
        }

        sb.append("## Customers (").append(customerLines.size()).append(")\n");
        sb.append(String.join("\n", customerLines));
        sb.append("\n\n");

        sb.append("## Owners (").append(ownerLines.size()).append(")\n");
        sb.append(String.join("\n", ownerLines));
        sb.append("\n\n");

        final List<Restaurant> restaurants = this.restaurantRepository.findAll();
        final List<String> restaurantLines = new ArrayList<>();
        for (Restaurant restaurant : restaurants) {
            restaurantLines.add(restaurant.toString());
        }

        sb.append("## Restaurants (").append(restaurantLines.size()).append(")\n");
        sb.append(String.join("\n", restaurantLines));
        sb.append("\n");

        return sb.toString();
    }
}