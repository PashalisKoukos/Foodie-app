package gr.dit.hua.Foodie.core.port.impl;

import gr.dit.hua.Foodie.core.port.SmsNotificationPort;
import gr.dit.hua.Foodie.core.port.dto.SendSmsRequest;
import gr.dit.hua.Foodie.core.port.dto.SendSmsResult;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class SmsNotificationPortImpl implements SmsNotificationPort {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String apiToken;

    public SmsNotificationPortImpl(
            @Qualifier("restTemplate") final RestTemplate restTemplate,
            @Value("${foodie.noc.base-url:http://localhost:8081}") final String baseUrl,
            @Value("${foodie.noc.api-token:}") final String apiToken) {
        if (restTemplate == null) throw new NullPointerException();
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.apiToken = apiToken;
    }

    @Override
    public boolean sendSms(final String e164, final String content) {
        if (e164 == null) throw new NullPointerException();
        if (e164.isBlank()) throw new IllegalArgumentException();
        if (content == null) throw new NullPointerException();
        if (content.isBlank()) throw new IllegalArgumentException();

        // Configures the HTTP headers and authentication.
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (apiToken != null && !apiToken.isBlank()) {
            headers.setBearerAuth(apiToken);
        }

        // Creates the SMS request payload.
        SendSmsRequest body = new SendSmsRequest(e164, content);
        HttpEntity<SendSmsRequest> entity = new HttpEntity<>(body, headers);

        try {
            // Sends the SMS request to the external NOC service.
            ResponseEntity<SendSmsResult> response =
                    restTemplate.postForEntity(baseUrl + "/api/v1/sms", entity, SendSmsResult.class);

            // Checks the response and returns the sending result.
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody().sent();
            }
            throw new IllegalStateException("Unexpected NOC response: " + response.getStatusCode());
        } catch (HttpClientErrorException ex) {
            // Handles requests rejected by the external service.
            throw new IllegalArgumentException("NOC rejected SMS request: " + ex.getStatusCode(), ex);
        } catch (RestClientException ex) {
            // Handles communication errors with the external service.
            throw new IllegalStateException("NOC SMS timeout or service error", ex);
        }
    }
}
