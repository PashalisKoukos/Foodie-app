package gr.dit.hua.Foodie.core.fixture;

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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Component responsible for seeding initial restaurants and their items into the database.
 * AI helped us to code the fake values
 */
@Component
@Order(2) // running after the userFixture ( Order(1) )
public class RestaurantFixture implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestaurantFixture.class);

    private final RestaurantRepository restaurantRepository;
    private final RestaurantItemRepository restaurantItemRepository;
    private final UserRepository userRepository;

    public RestaurantFixture(RestaurantRepository restaurantRepository,
                             RestaurantItemRepository restaurantItemRepository,
                             UserRepository userRepository) {
        this.restaurantRepository = restaurantRepository;
        this.restaurantItemRepository = restaurantItemRepository;
        this.userRepository = userRepository;
    }

    /**
     * initialize restaurants and link them with their owner
     * @param args incoming main method arguments
     */
    @Override
    @Transactional
    public void run(String... args) {
        // Idempotency check: check if already exists, then skipping seeding
        if (this.restaurantRepository.count() > 0) {
            LOGGER.info("Restaurants already exist in the database. Skipping restaurant fixture seeding.");
            return;
        }

        // searching all users to find the owners
        List<User> allUsers = this.userRepository.findAll();
        List<User> owners = new java.util.ArrayList<>();

        for (User user : allUsers) {
            if (user.getRole() == Role.OWNER) { // if owner
                owners.add(user);
            }
        }

        // Now we have the owners
        if (owners.isEmpty()) {
            LOGGER.warn("No owners found in the database. Cannot seed restaurants.");
            return;
        }

        LOGGER.info("Seeding restaurants and menu items for {} owners...", owners.size());

        /*
         * Initializes a constant fake data generator (JavaFaker) using US English
         * locale (en-US) and a fixed seed (100) for reproducible test results.
         */
        final Faker faker = new Faker(new Locale("en-US"), new Random(100));

        // random values
        String[] cuisines = {"Greek", "Italian", "Burgers", "Mexican", "Asian", "Pizza", "Souvlaki", "Bakery","Chinese"};
        String[] regions = {"Athens", "Glyfada", "Kifisia", "Marousi", "Chalandri","Tavros","Kallithea"};

        int restaurantCount = 0;
        Random random = new Random();
        for (User owner : owners) { // for each owner
            // choose randomly
            String cuisine = cuisines[random.nextInt(cuisines.length)];
            String region = regions[random.nextInt(regions.length)];

            String restaurantName = faker.company().name() + " " + cuisine;
            String address = faker.address().streetName() + " " + faker.number().digit();
            boolean isOpen = faker.bool().bool();
            double minOrder = faker.number().randomDouble(2, 5, 15); // 5... 15 €

            Restaurant restaurant = new Restaurant(
                    restaurantName,
                    cuisine,
                    region,
                    address,
                    isOpen,
                    minOrder,
                    owner
            );

            Restaurant savedRestaurant = this.restaurantRepository.save(restaurant);
            restaurantCount++;

            // create from 4 to 6 products
            int itemsCount = 4 + random.nextInt(3); // 4, 5 or 6
            for (int j = 0; j < itemsCount; j++) {
                String itemName = faker.commerce().productName();
                String description = faker.food().ingredient() + ", " + faker.food().spice();
                double price = faker.number().randomDouble(2, 3, 20); // 3... 20 €

                RestaurantItem item = new RestaurantItem(
                        itemName,
                        description,
                        price,
                        true, // available
                        savedRestaurant
                );

                this.restaurantItemRepository.save(item);
            }
        }

        LOGGER.info("Successfully seeded {} restaurants with their menu items.", restaurantCount);
    }
}