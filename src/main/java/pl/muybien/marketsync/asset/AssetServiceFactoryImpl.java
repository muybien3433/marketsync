package pl.muybien.marketsync.asset;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.muybien.marketsync.handler.AssetNotFoundException;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class AssetServiceFactoryImpl implements AssetServiceFactory {

    private final Map<String, AssetService> services;

    @Override
    public AssetService getService(String assetName) {
        AssetService service = services.get(assetName.toLowerCase());
        if (service == null) {
            throw new AssetNotFoundException("No service found for type: %s".formatted(assetName));
        }
        return service;
    }
}
