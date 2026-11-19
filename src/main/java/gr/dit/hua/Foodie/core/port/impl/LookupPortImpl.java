package gr.dit.hua.Foodie.core.port.impl;


import gr.dit.hua.Foodie.config.RestApiClientConfig;
import gr.dit.hua.Foodie.core.model.Role;
import gr.dit.hua.Foodie.core.port.LookupPort;
import gr.dit.hua.Foodie.core.port.dto.LookupResult;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

/**
 * Default implementation of {@link LookupPort}. */
@Service
public class LookupPortImpl implements LookupPort {

    private final RestTemplate restTemplate ;

    public LookupPortImpl(final RestTemplate restTemplate) {
        if (restTemplate == null) throw new NullPointerException();
        this.restTemplate = restTemplate;
    }

    @Override
    public Optional<Role> lookup(final String username){
        if (username == null) throw new NullPointerException();
        if (username.isBlank()) throw new IllegalArgumentException();

        // HTTP Request
        // --------------------------------------------------
        final String baseUrl = RestApiClientConfig.BASE_URL;
        final String url = baseUrl + "/api/v1/lookups/" + username;
        final ResponseEntity<LookupResult> response = this.restTemplate.getForEntity(url, LookupResult.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            final LookupResult lookupResult = response.getBody();
            if (lookupResult == null) throw new NullPointerException();
            return Optional.ofNullable(lookupResult.role());
        }

        throw new RuntimeException("External service responded with " + response.getStatusCode());
    }
}
