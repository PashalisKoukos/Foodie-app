package gr.dit.hua.Foodie.core.port.impl;

import gr.dit.hua.Foodie.config.RestApiClientConfig;
import gr.dit.hua.Foodie.core.port.SmsNotificationPort;
import gr.dit.hua.Foodie.core.port.dto.SendSmsRequest;
import gr.dit.hua.Foodie.core.port.dto.SendSmsResult;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SmsNotificationPortImpl implements SmsNotificationPort {

    private final RestTemplate restTemplate;

    public SmsNotificationPortImpl(final RestTemplate restTemplate) {
        if (restTemplate == null) throw new NullPointerException();
        this.restTemplate = restTemplate;
    }

    @Override
    public boolean sendSms(final String e164, final String content){

        if (e164 == null) throw new NullPointerException();
        if (e164.isBlank()) throw new IllegalArgumentException();
        if (content == null) throw new NullPointerException();
        if (content.isBlank()) throw new IllegalArgumentException();

        // Configures the HTTP headers.

        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        // Creates the SMS request payload.
        final SendSmsRequest body = new SendSmsRequest(e164, content);

        // Alternative JSON payload using a Map:
        // Spring automatically handles JSON serialization.
        // final Map<String, Object> body = Map.of("e164", e164, "body", content);

        // Sends the HTTP request to the external SMS service.
        final String baseUrl = RestApiClientConfig.BASE_URL;
        final String url = baseUrl + "/api/v1/sms";
        final HttpEntity<SendSmsRequest> entity = new HttpEntity<>(body, httpHeaders);
        final ResponseEntity<SendSmsResult> response = this.restTemplate.postForEntity(url, entity, SendSmsResult.class);

        // Checks the response and returns the SMS sending result.
        if (response.getStatusCode().is2xxSuccessful()) {
            final SendSmsResult sendSmsResult = response.getBody();
            if (sendSmsResult == null) throw new NullPointerException();
            return sendSmsResult.sent();
        }

        throw new RuntimeException("External service responded with " + response.getStatusCode());
    }
}
