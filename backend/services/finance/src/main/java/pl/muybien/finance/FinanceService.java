package pl.muybien.finance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.muybien.finance.crypto.CryptoService;
import pl.muybien.finance.currency.CurrencyService;
import pl.muybien.finance.stock.StockService;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FinanceService {

    private final StockService stockService;
    private final CryptoService cryptoService;
    private final CurrencyService currencyService;
    private final FinanceRepository repository;

    FinanceResponse fetchFinance(String assetType, String uri) {
        AssetType type = AssetType.fromString(assetType);
        return switch (type) {
            case CRYPTOS -> cryptoService.fetchCrypto(uri, type);
            case STOCKS -> stockService.fetchStock(uri, type);
            default -> null;
        };
    }

    BigDecimal findExchangeRate(CurrencyType from, CurrencyType to) {
        return currencyService.getCurrencyPairExchange(from, to);
    }

    Set<FinanceDetail> displayAvailableFinance(String assetType) {
        return repository.findFinanceByAssetTypeIgnoreCase(assetType.toLowerCase())
                .map(Finance::getFinanceDetails)
                .orElse(Collections.emptySet());
    }
}
