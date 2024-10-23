package pl.muybien.notifier.currency.crypto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class CryptoServiceFactoryImpl implements CryptoServiceFactory {

    private final Map<String, CryptoService> services;

    @Override
    public CryptoService getService(String cryptoType) {
        CryptoService service = services.get(cryptoType.toLowerCase());
        if (service == null) {
            throw new IllegalArgumentException("No CryptoService found for type: " + cryptoType);
        }
        return service;
    }
}
