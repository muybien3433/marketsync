package pl.muybien.marketsync.currency;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.muybien.marketsync.handler.CurrencyNotFoundException;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class CurrencyServiceFactoryImpl implements CurrencyServiceFactory {

    private final Map<String, CurrencyService> services;

    @Override
    public CurrencyService getService(String currencyName) {
        CurrencyService service = services.get(currencyName.toLowerCase());
        if (service == null) {
            throw new CurrencyNotFoundException("No service found for type: %s".formatted(currencyName));
        }
        return service;
    }
}
