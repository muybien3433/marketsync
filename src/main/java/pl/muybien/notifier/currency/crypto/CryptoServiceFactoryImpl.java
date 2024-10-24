package pl.muybien.notifier.currency.crypto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.muybien.notifier.handler.CryptoNotFoundException;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class CryptoServiceFactoryImpl implements CryptoServiceFactory {

    private final Map<String, CryptoService> services;

    @Override
    public CryptoService getService(String cryptoName) {
        CryptoService service = services.get(cryptoName.toLowerCase());
        if (service == null) {
            throw new CryptoNotFoundException("No service found for type: %s".formatted(cryptoName));
        }
        return service;
    }
}
