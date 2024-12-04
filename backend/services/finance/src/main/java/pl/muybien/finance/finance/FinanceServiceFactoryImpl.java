package pl.muybien.finance.finance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.muybien.finance.exception.FinanceNotFoundException;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class FinanceServiceFactoryImpl implements FinanceServiceFactory {

    private final Map<String, FinanceService> services;

    @Override
    public FinanceService getService(String currencyName) {
        FinanceService service = services.get(currencyName.toLowerCase());
        if (service == null) {
            throw new FinanceNotFoundException("No service found for: %s".formatted(currencyName));
        }
        return service;
    }
}
