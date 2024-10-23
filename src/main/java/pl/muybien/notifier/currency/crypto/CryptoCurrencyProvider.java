package pl.muybien.notifier.currency.crypto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class CryptoCurrencyProvider {

    private final WebClient webClient;

    public Crypto fetchCurrencyByUri(String uri) {
        Crypto currentCrypto = webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(CryptoResponse.class)
                .map(CryptoMapper::mapToCrypto)
                .block();
        if (currentCrypto == null) {
            throw new RuntimeException("WebClient returned null"); // TODO: Better exception handling
        }
        return currentCrypto;
    }
}
