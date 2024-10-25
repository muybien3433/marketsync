package pl.muybien.marketsync.asset;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.muybien.marketsync.handler.AssetNotFoundException;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class AssetProviderFactoryImpl implements AssetProviderFactory {

    private final Map<String, AssetProvider> assetProviders;

    @Override
    public AssetProvider getProvider(String uri) {
        AssetProvider provider = assetProviders.get(uri.toLowerCase());
        if (provider == null) {
            throw new AssetNotFoundException("No provider found for: %s".formatted(uri));
        }
        return provider;
    }
}