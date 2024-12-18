package pl.muybien.finance.finance.currency;

import org.springframework.stereotype.Service;
import pl.muybien.finance.finance.Finance;
import reactor.core.publisher.Mono;

@Service
public class CurrencyService {

    public Mono<Finance> convertToDesiredCurrency(Mono<Finance> currentAssetPriceInUsd, String currency) {
        return null;
    }
}
