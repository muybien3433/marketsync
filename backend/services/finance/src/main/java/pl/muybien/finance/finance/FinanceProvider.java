package pl.muybien.finance.finance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.muybien.finance.finance.currency.CurrencyService;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FinanceProvider {

    private final FinanceServiceFactory financeServiceFactory;
    private final CurrencyService currencyService;

    public Mono<Finance> fetchFinance(String uri) {
        return financeServiceFactory.getService(uri).fetchCurrentFinance();
    }

    public Mono<Finance> fetchFinanceWithDesiredCurrency(String uri, String currency) {
        var currentAssetPriceInUsd = financeServiceFactory.getService(uri).fetchCurrentFinance();
        return currencyService.convertToDesiredCurrency(currentAssetPriceInUsd, currency);
    }
}
