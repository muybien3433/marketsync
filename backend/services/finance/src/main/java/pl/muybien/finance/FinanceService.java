package pl.muybien.finance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.muybien.finance.crypto.CryptoService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FinanceService {

    private final FinanceFileReader financeFileReader;
    private final CryptoService cryptoService;

    FinanceResponse fetchFinance(String type, String uri, String currency) {
        return switch (type) {
            case "cryptos" -> cryptoService.fetchCrypto(uri, type, currency);
            case "stocks" -> null;
            default -> null;
        };
    }

    List<FinanceFileDTO> displayAvailableFinance(String type) {
        return financeFileReader.displayAvailableFinance(type);
    }
}
