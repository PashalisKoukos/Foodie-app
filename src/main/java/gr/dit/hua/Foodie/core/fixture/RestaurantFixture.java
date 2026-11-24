package gr.dit.hua.Foodie.core.fixture;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import gr.dit.hua.Foodie.core.model.Restaurant;
import gr.dit.hua.Foodie.core.model.RestaurantItem;
import gr.dit.hua.Foodie.core.model.Role;
import gr.dit.hua.Foodie.core.model.User;
import gr.dit.hua.Foodie.core.repository.RestaurantItemRepository;
import gr.dit.hua.Foodie.core.repository.RestaurantRepository;
import gr.dit.hua.Foodie.core.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Locale;

@Component
@Order(2)
public class RestaurantFixture implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestaurantFixture.class);

    private final RestaurantRepository restaurantRepository;
    private final RestaurantItemRepository restaurantItemRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RestaurantFixture(RestaurantRepository restaurantRepository, RestaurantItemRepository restaurantItemRepository, UserRepository userRepository) {
        this.restaurantRepository = restaurantRepository;
        this.restaurantItemRepository = restaurantItemRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        // Check if restaurants already exist
        if (restaurantRepository.count() > 0) {
            LOGGER.info("Restaurants already exist. Skipping seeding.");
            return;
        }

        // Find restaurant owners
        List<User> allUsers = userRepository.findAll();

        List<User> owners = new ArrayList<>();

        for (User user : allUsers) {
            if (user.getRole() == Role.OWNER) {
                owners.add(user);
            }
        }

        if (owners.isEmpty()) {
            LOGGER.warn("No owners found. Cannot seed restaurants.");
            return;
        }

        LOGGER.info("Seeding restaurants and menu items for {} owners...", owners.size());

        // Load menu items from JSON ( ΑΙ provided )
        Map<String, List<MenuItemData>> menuData = objectMapper.readValue(new ClassPathResource("dishes.json").getInputStream(), new TypeReference<Map<String, List<MenuItemData>>>() {});

        final Faker faker = new Faker(
                new Locale("en-US"),
                new Random(100)
        );

        String[] cuisines = {
                "Greek", "Italian", "Burgers", "Mexican",
                "Asian", "Pizza", "Souvlaki", "Bakery", "Chinese"
        };

        String[] regions = {
                "Athens", "Glyfada", "Kifisia", "Marousi",
                "Chalandri", "Tavros", "Kallithea"
        };

        int restaurantCount = 0;

        Random random = new Random(100);

        for (User owner : owners) {

            // Random restaurant cuisine and region
            String cuisine = cuisines[random.nextInt(cuisines.length)];
            String region = regions[random.nextInt(regions.length)];

            String restaurantName = faker.company().name() + " " + cuisine;

            String address = faker.address().streetName() + " " + faker.number().digit();

            boolean isOpen = faker.bool().bool();

            BigDecimal minOrder = BigDecimal.valueOf(faker.number().randomDouble(2, 5, 8));

            Restaurant restaurant = new Restaurant(
                    restaurantName,
                    cuisine,
                    region,
                    address,
                    isOpen,
                    minOrder,
                    owner
            );

            Restaurant savedRestaurant = restaurantRepository.save(restaurant);

            restaurantCount++;

            // Get menu items for the restaurant's cuisine
            List<MenuItemData> availableItems = menuData.get(cuisine);

            if (availableItems == null || availableItems.isEmpty()) {
                LOGGER.warn("No menu items found for cuisine: {}", cuisine);
                continue;
            }

            // Select a random number of items between 10 and 15
            int itemsCount = Math.min(10 + random.nextInt(6), availableItems.size());

            // Shuffle items to avoid duplicates
            List<MenuItemData> shuffledItems = new ArrayList<>(availableItems);

            java.util.Collections.shuffle(shuffledItems, random);

            // Create restaurant menu items
            for (int j = 0; j < itemsCount; j++) {

                MenuItemData menuItem = shuffledItems.get(j);

                RestaurantItem item = new RestaurantItem(
                        menuItem.getName(),
                        menuItem.getDescription(),
                        menuItem.getPrice(),
                        true,
                        savedRestaurant
                );

                restaurantItemRepository.save(item);
            }

            LOGGER.info(
                    "Created restaurant '{}' with {} menu items.",
                    restaurantName,
                    itemsCount
            );
        }

        LOGGER.info(
                "Successfully seeded {} restaurants with menu items.",
                restaurantCount
        );
    }
}
