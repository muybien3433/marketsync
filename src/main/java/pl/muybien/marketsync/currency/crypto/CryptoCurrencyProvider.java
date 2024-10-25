package pl.muybien.marketsync.currency.crypto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import pl.muybien.marketsync.currency.Currency;
import pl.muybien.marketsync.currency.CurrencyProvider;
import pl.muybien.marketsync.handler.CurrencyNotFoundException;

@Component
@RequiredArgsConstructor
public class CryptoCurrencyProvider implements CurrencyProvider {

    private final WebClient webClient;

    public Currency fetchCurrency(String uri) {
        Currency currentCrypto = webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(CryptoResponse.class)
                .map(CryptoMapper::mapToCrypto)
                .block();
        if (currentCrypto == null) {
            throw new CurrencyNotFoundException("Crypto data not found for URI: %s".formatted(uri));
        }
        return currentCrypto;
    }
}
