package pl.muybien.marketsync.asset;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.muybien.marketsync.handler.AssetNotFoundException;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AssetProviderFactoryImpl implements AssetProviderFactory {

    private final Map<String, AssetProvider> assetProviders;

    @Override
    public AssetProvider getProvider(String assetName) {
        AssetProvider provider = assetProviders.get(assetName.toLowerCase());
        if (provider == null) {
            throw new AssetNotFoundException("No provider found for: %s".formatted(assetName));
        }
        return provider;
    }
}