package pl.muybien.subscriptionservice.finance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.muybien.subscriptionservice.handler.FinanceNotFoundException;

import java.util.Map;

//@Service
//@RequiredArgsConstructor
//public class FinanceProviderFactoryImpl implements FinanceProviderFactory {
//
//    private final Map<String, FinanceProvider> financeProviders;
//
//    @Override
//    public FinanceProvider getProvider(String financeName) {
//        FinanceProvider provider = financeProviders.get(financeName.toLowerCase());
//        if (provider == null) {
//            throw new FinanceNotFoundException("No provider found for: %s".formatted(financeName));
//        }
//        return provider;
//    }
//}