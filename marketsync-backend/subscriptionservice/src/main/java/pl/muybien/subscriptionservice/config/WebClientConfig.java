package pl.muybien.subscriptionservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

//    @Value("${crypto.api.uri}")
//    private String cryptoApiUri;
//
//    @Bean
//    WebClient webClient(WebClient.Builder builder) {
//        return builder
//                .baseUrl(cryptoApiUri)
//                .build();
//    }
}
