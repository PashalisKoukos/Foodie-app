package gr.dit.hua.Foodie.config;

import gr.dit.hua.Foodie.core.security.AuthSuccessHandler;
import gr.dit.hua.Foodie.core.security.JwtAuthenticationFilter;
import gr.dit.hua.Foodie.web.rest.error.RestApiAccessDeniedHandler;
import gr.dit.hua.Foodie.web.rest.error.RestApiAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration.
 */


@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Enables @Preauthorize
public class SecurityConfig {

    /**
     * API chain {@code "/api/**"} (stateless, JWT).
     */
    @Bean
    @Order(1)
    public SecurityFilterChain apiChain(final HttpSecurity http,
                                        final JwtAuthenticationFilter jwtAuthenticationFilter,
                                        final RestApiAuthenticationEntryPoint restApiAuthenticationEntryPoint,
                                        final RestApiAccessDeniedHandler restApiAccessDeniedHandler) throws Exception {
        http
                .securityMatcher("/api/v1/**")
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/client-tokens").permitAll()
                        .requestMatchers("/api/v1/**").authenticated()
                )
                .exceptionHandling(exh -> exh
                        .authenticationEntryPoint(restApiAuthenticationEntryPoint)
                        .accessDeniedHandler(restApiAccessDeniedHandler)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable);
        return http.build();
    }

    /**
     * UI chain {@code "/**"} (stateful, cookie based).
     */
    @Bean
    @Order(2)
    public SecurityFilterChain uiChain(final HttpSecurity http) throws Exception {
        http
                .securityMatcher("/**")
                // Το αφήνουμε ως σχόλιο προσωρινά... TODO configure.
                // .csrf(csrf -> csrf.ignoringRequestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**"))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()
                        .requestMatchers("/", "/login", "/register").permitAll() // Public
                        .requestMatchers("/profile", "/logout").authenticated() // Private
                        .anyRequest().permitAll() // the rest
                )
                .formLogin(form -> form
                        .loginPage("/login") // custom login page (see login.html)
                        .loginProcessingUrl("/login") // POST request target (handled by Spring Security)
                        .defaultSuccessUrl("/profile", true)
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // POST request target (handled by Spring Security)
                        .logoutSuccessUrl("/login?logout")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .permitAll()
                )
                .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }

    // Login Security :
    private final AuthSuccessHandler authSuccessHandler;

    public SecurityConfig(final AuthSuccessHandler authSuccessHandler) {
        this.authSuccessHandler = authSuccessHandler;
    }

    // Configures the password encoder using BCrypt.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configures HTTP security, access permissions, login and logout.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/","/home", "/register", "/login", "/css/**", "/js/**", "/img/**","/error").permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/diagnostics").permitAll()
                        .requestMatchers("/owner/**").hasRole("OWNER")
                        .requestMatchers("/customer/**").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.GET, "/restaurants/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler(authSuccessHandler)
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        // Allows all users to access the logout endpoint.
                        .permitAll()
                );

        return http.build();
    }
}