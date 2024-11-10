package pl.muybien.marketsync.finance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class FinanceServiceFactoryImpl implements FinanceServiceFactory {

    private final Map<String, FinanceService> services;

    @Override
    public FinanceService getService(String financeName) {
        FinanceService service = services.get(financeName.toLowerCase());
        if (service == null) {
            throw new FinanceNotFoundException("No service found for type: %s".formatted(financeName));
        }
        return service;
    }
}
