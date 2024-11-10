package pl.muybien.subscriptionservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class BeansConfig {

    @Value("${crypto.api.uri}")
    private String cryptoApiUri;

    @Bean
    WebClient webClient(WebClient.Builder builder) {
        return builder
                .baseUrl(cryptoApiUri)
                .build();
    }
}
