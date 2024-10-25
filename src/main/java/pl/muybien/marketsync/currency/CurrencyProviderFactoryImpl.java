package pl.muybien.marketsync.currency;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.muybien.marketsync.handler.CurrencyNotFoundException;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class CurrencyProviderFactoryImpl implements CurrencyProviderFactory {

    private final Map<String, CurrencyProvider> currencyProviders;

    @Override
    public CurrencyProvider getProvider(String uri) {
        CurrencyProvider provider = currencyProviders.get(uri.toLowerCase());
        if (provider == null) {
            throw new CurrencyNotFoundException("No provider found for: %s".formatted(uri));
        }
        return provider;
    }
}