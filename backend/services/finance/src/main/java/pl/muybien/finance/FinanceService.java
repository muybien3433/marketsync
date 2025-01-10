package pl.muybien.finance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.muybien.finance.crypto.CoinmarketcapService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FinanceService {

    private final CoinmarketcapService coinmarketcapService;
    private final FinanceFileReader financeFileReader;

    FinanceResponse fetchFinance(String type, String uri) {
        return switch (type) {
            case "cryptos" -> coinmarketcapService.fetchFinance(uri, type);
            case "stocks" -> null;
            default -> null;
        };
    }

    List<FinanceFileDTO> displayAvailableFinance(String type) {
        return financeFileReader.displayAvailableFinance(type);
    }
}
