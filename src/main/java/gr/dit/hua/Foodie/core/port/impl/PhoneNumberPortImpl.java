package gr.dit.hua.Foodie.core.port.impl;

import gr.dit.hua.Foodie.core.port.PhoneNumberPort;
import gr.dit.hua.Foodie.core.port.dto.PhoneNumberValidationResult;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Default implementation of PhoneNumberPort.
 */
@Service
public class PhoneNumberPortImpl implements PhoneNumberPort {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final int maxRetries;

    public PhoneNumberPortImpl(
            @Qualifier("restTemplate") final RestTemplate restTemplate,
            @Value("${foodie.noc.base-url:http://localhost:8081}") final String baseUrl,
            @Value("${foodie.noc.max-get-retries:2}") final int maxRetries) {
        if (restTemplate == null) throw new NullPointerException();
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.maxRetries = Math.max(0, maxRetries);
    }

    @Override
    public PhoneNumberValidationResult validate(final String rawPhoneNumber) {
        if (rawPhoneNumber == null) throw new NullPointerException();
        if (rawPhoneNumber.isBlank()) throw new IllegalArgumentException();

        // Builds the URL for the phone number validation request.
        final String url = baseUrl + "/api/v1/phone-numbers/" + rawPhoneNumber + "/validations";

        // Tracks the number of request attempts.
        int attempts = 0;
        while (true) {
            attempts++;
            try {
                // Sends a GET request to the external service.
                final ResponseEntity<PhoneNumberValidationResult> response
                        = this.restTemplate.getForEntity(url, PhoneNumberValidationResult.class);

                if (response.getStatusCode().is2xxSuccessful()) {
                    final PhoneNumberValidationResult phoneNumberValidationResult = response.getBody();
                    if (phoneNumberValidationResult == null) throw new NullPointerException();
                    return phoneNumberValidationResult;
                }

                throw new RuntimeException("External service responded with " + response.getStatusCode());
            } catch (HttpClientErrorException ex) {
                // Handles requests rejected by the external service.
                throw new IllegalArgumentException("NOC phone validation rejected request: " + ex.getStatusCode(), ex);
            } catch (RestClientException ex) {
                // Retries the request until the maximum number of attempts is reached.
                if (attempts > maxRetries) {
                    throw new IllegalStateException("NOC phone validation timeout or service error after retries", ex);
                }
            }
        }
    }
}