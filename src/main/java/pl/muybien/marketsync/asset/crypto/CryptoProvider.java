package pl.muybien.marketsync.asset.crypto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pl.muybien.marketsync.asset.Asset;
import pl.muybien.marketsync.asset.AssetProvider;
import pl.muybien.marketsync.handler.AssetNotFoundException;

@Service("crypto")
@RequiredArgsConstructor
public class CryptoProvider implements AssetProvider {

    private final WebClient webClient;
    private final CryptoMapper cryptoMapper;

    public Asset fetchAsset(String uri) {
        Asset currentCrypto = webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(CryptoResponse.class)
                .map(cryptoMapper::mapToCrypto)
                .block();
        if (currentCrypto == null) {
            throw new AssetNotFoundException("Crypto data not found for URI: %s".formatted(uri));
        }
        return currentCrypto;
    }
}
