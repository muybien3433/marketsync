package pl.muybien.finance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.muybien.finance.crypto.CryptoService;
import pl.muybien.finance.currency.CurrencyService;
import pl.muybien.finance.currency.CurrencyType;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FinanceService {

    private final FinanceFileReader financeFileReader;
    private final CryptoService cryptoService;
    private final CurrencyService currencyService;

    FinanceResponse fetchFinance(String assetType, String uri, String currency) {
        return switch (assetType) {
            case "cryptos" -> cryptoService.fetchCrypto(uri, assetType, currency);
            default -> null;
        };
    }

    FinanceResponse fetchFinance(String assetType, String uri) {
        return switch (assetType) {
            case "cryptos" -> cryptoService.fetchCrypto(uri, assetType);
            default -> null;
        };
    }

    List<FinanceFileDTO> displayAvailableFinance(String assetType) {
        return financeFileReader.displayAvailableFinance(assetType);
    }

    public BigDecimal findExchangeRate(String from, String to) {
        return currencyService.getCurrencyPairExchange(CurrencyType.fromString(from), CurrencyType.fromString(to));
    }
}
