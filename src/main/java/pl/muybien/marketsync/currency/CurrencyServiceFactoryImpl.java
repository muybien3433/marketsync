package pl.muybien.marketsync.currency.crypto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.muybien.marketsync.currency.CurrencyService;
import pl.muybien.marketsync.handler.CryptoNotFoundException;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class CurrencyServiceFactoryImpl implements CryptoServiceFactory {

    private final Map<String, CurrencyService> services;

    @Override
    public CurrencyService getService(String currencyName) {
        CurrencyService service = services.get(currencyName.toLowerCase());
        if (service == null) {
            throw new CryptoNotFoundException("No service found for type: %s".formatted(cryptoName));
        }
        return service;
    }
}
