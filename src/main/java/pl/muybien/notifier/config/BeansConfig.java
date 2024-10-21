package pl.muybien.notifier.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class BeansConfig {

    @Bean
    WebClient webClient(WebClient.Builder builder) {
        return builder
                .baseUrl("https://api.coincap.io/v2/assets/")
                .build();
    }
}
