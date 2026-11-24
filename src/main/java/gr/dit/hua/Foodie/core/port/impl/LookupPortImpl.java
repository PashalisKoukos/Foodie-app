package gr.dit.hua.Foodie.core.port.impl;

import gr.dit.hua.Foodie.core.model.Role;
import gr.dit.hua.Foodie.core.port.LookupPort;
import gr.dit.hua.Foodie.core.port.dto.LookupResult;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

/**
 * Default implementation of {@link LookupPort}.
 */
@Service
public class LookupPortImpl implements LookupPort {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final int maxRetries;

    public LookupPortImpl(
            @Qualifier("restTemplate") final RestTemplate restTemplate,
            @Value("${foodie.noc.base-url:http://localhost:8081}") final String baseUrl,
            @Value("${foodie.noc.max-get-retries:2}") final int maxRetries) {
        if (restTemplate == null) throw new NullPointerException();
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.maxRetries = Math.max(0, maxRetries);
    }

    @Override
    public Optional<Role> lookup(final String username) {
        if (username == null) throw new NullPointerException();
        if (username.isBlank()) throw new IllegalArgumentException();

        // Builds the URL for the username lookup request.
        final String url = baseUrl + "/api/v1/lookups/" + username;

        // Tracks the number of attempts.
        int attempts = 0;
        while (true) {
            attempts++;
            try {
                // Sends a GET request to the external service.
                final ResponseEntity<LookupResult> response =
                        this.restTemplate.getForEntity(url, LookupResult.class);

                if (response.getStatusCode().is2xxSuccessful()) {
                    final LookupResult lookupResult = response.getBody();
                    if (lookupResult == null) throw new NullPointerException();
                    return Optional.ofNullable(lookupResult.role());
                }

                throw new IllegalStateException("External service responded with " + response.getStatusCode());
            } catch (HttpClientErrorException ex) {
                // Returns an empty result when the username is not found.
                if (ex.getStatusCode().value() == 404) {
                    return Optional.empty();
                }
                throw new IllegalArgumentException("NOC lookup rejected request: " + ex.getStatusCode(), ex);
            } catch (RestClientException ex) {
                // Throws an exception when all retry attempts are exhausted.
                if (attempts > maxRetries) {
                    throw new IllegalStateException("NOC lookup timeout or service error after retries", ex);
                }
            }
        }
    }
}
