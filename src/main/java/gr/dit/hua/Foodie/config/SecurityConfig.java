
package gr.dit.hua.Foodie.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Defines a password encoder bean shared across the application.
    // BCrypt is used to securely hash passwords before storing them.
    // The same encoder is used during login to verify submitted passwords.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Defines the security filter chain that controls access to application endpoints.
    // It specifies which requests are public and which require authentication.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Configures authorization rules for incoming HTTP requests.
                .authorizeHttpRequests(auth -> auth
                        // Allows unauthenticated users to access the home page,
                        // registration and login pages, and static resources.
                        .requestMatchers("/", "/register", "/login", "/css/**", "/js/**", "/img/**").permitAll()

                        // Allows public access to the application health endpoint.
                        // This endpoint can be used to check whether the application is running.
                        .requestMatchers("/actuator/health").permitAll()

                        // Requires authentication for every other request.
                        .anyRequest().authenticated()
                )

                // Configures form-based authentication.
                .formLogin(form -> form
                        // Specifies the custom login page displayed to users.
                        .loginPage("/login")

                        // Defines the URL that processes submitted login credentials.
                        .loginProcessingUrl("/login")

                        // Redirects users to the home page after a successful login.
                        // The true parameter forces the redirect to this URL.
                        .defaultSuccessUrl("/home", true)

                        // Redirects users back to the login page if authentication fails.
                        .failureUrl("/login?error")

                        // Allows everyone to access the login page and submit login credentials.
                        .permitAll()
                )

                // Configures the logout functionality.
                .logout(logout -> logout
                        // Redirects users to the login page after successfully logging out.
                        .logoutSuccessUrl("/login?logout")

                        // Allows all users to access the logout endpoint.
                        .permitAll()
                );

        // Builds and returns the configured security filter chain.
        return http.build();
    }
}
