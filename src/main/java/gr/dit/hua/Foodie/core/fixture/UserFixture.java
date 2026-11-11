package gr.dit.hua.Foodie.core.fixture;

import gr.dit.hua.Foodie.core.model.Role;
import gr.dit.hua.Foodie.core.model.User;
import gr.dit.hua.Foodie.core.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Random;
import com.github.javafaker.Faker;

/**
 * Component responsible for seeding initial user data into the database.
 */
@Component
public final class UserFixture implements CommandLineRunner {

    // Logger instance for recording execution steps and information
    private static final Logger LOGGER = LoggerFactory.getLogger(UserFixture.class);

    // Repository used to interact with the user database table
    private final UserRepository userRepository;

    // Password encoder using the BCrypt hashing algorithm
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    public UserFixture(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Runs the seeding process
     */
    @Override
    public void run(String... args) {
        // Idempotency check: verify if users already exist in the database
        final long usersCount = this.userRepository.count();
        if (usersCount > 0) {
            LOGGER.info("Users already exist in the database. Skipping fixture seeding.");
            return;
        }

        LOGGER.info("Seeding initial users (customers and owners) with unique password hashes...");

        // Initialize JavaFaker with a specific locale and random seed for reproducible data
        final Faker fakeValuesGenerator = new Faker(new Locale("en-US"), new Random(42));

        final int customersToCreate = 150;
        final int ownersToCreate = 10;


        // Create customers loop
        for (int i = 0; i < customersToCreate; i++) {
            // Generate lowercase random first and last names
            String firstName = fakeValuesGenerator.name().firstName().toLowerCase();
            String lastName = fakeValuesGenerator.name().lastName().toLowerCase();

            // Construct unique username, email, and mobile phone number using index i
            String username = firstName + "_" + lastName + "_" + i;
            String email = lastName + "_" + i + "@example.com";
            String mobilePhoneNumber = fakeValuesGenerator.phoneNumber().cellPhone();

            // Generate a unique password for the customer and hash it with BCrypt
            String pass= "CusPass_" + username;
            String passwordHash = passwordEncoder.encode(pass);

            // Create the User entity with CUSTOMER role and save it to the repository
            User customer = new User(username, email, mobilePhoneNumber, passwordHash, Role.CUSTOMER);
            this.userRepository.save(customer);
        }

        // Create owners loop
        for (int i = 0; i < ownersToCreate; i++) {
            // Generate lowercase random first and last names for owners
            String firstName = fakeValuesGenerator.name().firstName().toLowerCase();

            // Construct unique owner username, email, and mobile phone number
            String username = "owner_" + firstName + "_" + i;
            String email = "owner." + firstName + "_" + i + "@foodie.com";
            String mobilePhoneNumber = fakeValuesGenerator.phoneNumber().cellPhone();

            // Generate a unique password for the owner and hash it with BCrypt
            String pass = "ownerPass_" + username;
            String passwordHash = passwordEncoder.encode(pass);

            // Create the User entity with OWNER role and save it to the repository
            User owner = new User(username, email, mobilePhoneNumber, passwordHash, Role.OWNER);
            this.userRepository.save(owner);
        }

        // Log successful seeding summary
        LOGGER.info("Successfully seeded {} customers and {} owners with unique credentials.", customersToCreate, ownersToCreate);
    }
}