package pl.muybien.finance;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.muybien.finance.crypto.coinmarketcap.CoinmarketcapService;

@RestController
@RequestMapping("/api/v1/finances/cryptos")
@RequiredArgsConstructor
public class FinanceController {

    private final CoinmarketcapService coinmarketcapService;

    @GetMapping("/{uri}")
    public ResponseEntity<Finance> getFinance(
            @PathVariable String uri
    ) {
        return ResponseEntity.ok(coinmarketcapService.fetchCrypto(uri));
    }

    @GetMapping
    public ResponseEntity<String> displayAvailableCrypto() {
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
