package pl.muybien.finance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.muybien.finance.crypto.CryptoService;
import pl.muybien.finance.currency.CurrencyService;
import pl.muybien.finance.currency.CurrencyType;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FinanceService {

    private static final String cryptos = "cryptos";

    private final CryptoService cryptoService;
    private final CurrencyService currencyService;
    private final FinanceRepository repository;

    FinanceResponse fetchFinance(String assetType, String uri, String currency) {
        return switch (assetType) {
            case cryptos -> cryptoService.fetchCrypto(uri, assetType, currency);
            default -> null;
        };
    }

    FinanceResponse fetchFinance(String assetType, String uri) {
        return switch (assetType) {
            case cryptos -> cryptoService.fetchCrypto(uri, assetType);
            default -> null;
        };
    }

    BigDecimal findExchangeRate(String from, String to) {
        CurrencyType fromCurrency = CurrencyType.fromString(from);
        CurrencyType toCurrency = CurrencyType.fromString(to);

        return currencyService.getCurrencyPairExchange(fromCurrency, toCurrency);
    }

    Set<FinanceDetail> displayAvailableFinance(String assetType) {
        return repository.findFinanceByAssetType(assetType)
                .map(Finance::getFinanceDetails)
                .orElse(Collections.emptySet());
    }
}
