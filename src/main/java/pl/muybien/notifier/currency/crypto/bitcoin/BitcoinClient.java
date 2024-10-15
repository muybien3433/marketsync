package pl.muybien.notifier.currency.crypto.bitcoin;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
public class BitcoinClient {

    private final WebClient webClient;

    public BitcoinClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("https://api.coincap.io/v2/assets").build();
    }

    @Bean
    public void run() {
        Flux<Bitcoin> btc = find();
        btc.subscribe(System.out::println,
                error -> System.err.println("Error: " + error.getMessage()));
    }

    public Flux<Bitcoin> find() {
        return webClient.get()
                .uri("/bitcoin")
                .retrieve()
                .bodyToFlux(BitcoinResponse.class)
                .map(BitcoinResponse::data);
    }
}
