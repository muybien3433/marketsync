package pl.muybien.finance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.muybien.finance.crypto.CryptoService;
import pl.muybien.finance.currency.CurrencyService;
import pl.muybien.finance.stock.StockService;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FinanceService {

    private final StockService stockService;
    private final CryptoService cryptoService;
    private final CurrencyService currencyService;
    private final FinanceRepository repository;
    private final FinanceDetailDTOMapper mapper;

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

    Set<FinanceDetailDTO> displayAvailableFinance(String assetType) {
        return repository.findFinanceByAssetTypeIgnoreCase(assetType.toLowerCase())
                .map(Finance::getFinanceDetails)
                .map(de -> de.stream()
                        .map(mapper::toDTO)
                        .collect(Collectors.toSet()))
                .orElse(Collections.emptySet());
    }
}
