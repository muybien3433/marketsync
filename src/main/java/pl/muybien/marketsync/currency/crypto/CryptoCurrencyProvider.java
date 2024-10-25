package pl.muybien.marketsync.currency.crypto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import pl.muybien.marketsync.handler.CryptoNotFoundException;

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
            throw new CryptoNotFoundException("Crypto data not found for URI: %s".formatted(uri));
        }
        return currentCrypto;
    }
}
