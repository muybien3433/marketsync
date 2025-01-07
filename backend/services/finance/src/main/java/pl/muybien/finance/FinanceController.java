package pl.muybien.finance;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.muybien.finance.crypto.coinmarketcap.CoinmarketcapService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/finances")
@RequiredArgsConstructor
public class FinanceController {

    private final CoinmarketcapService coinmarketcapService;
    private final FinanceFileReader financeFileReader;

    @GetMapping("/cryptos/{uri}")
    public ResponseEntity<FinanceResponse> getFinance(
            @PathVariable String uri
    ) {
        return ResponseEntity.ok(coinmarketcapService.fetchCrypto(uri));
    }

    @GetMapping("/{type}")
    public ResponseEntity<List<FinanceFileDTO>> displayAvailableFinance(
            @PathVariable String type
    ) {
        return ResponseEntity.ok(financeFileReader.displayAvailableFinance(type));
    }
}
