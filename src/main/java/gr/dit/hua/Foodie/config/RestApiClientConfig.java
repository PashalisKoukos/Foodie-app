package gr.dit.hua.Foodie.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestApiClientConfig {

    @Bean
    public RestTemplate restTemplate(@Value("${foodie.noc.connect-timeout-ms:2000}") int connectTimeoutMs,
                                     @Value("${foodie.noc.read-timeout-ms:3000}") int readTimeoutMs) {

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofMillis(connectTimeoutMs));
        factory.setReadTimeout(Duration.ofMillis(readTimeoutMs));

        return new RestTemplate(factory);
    }
}