package pl.muybien.finance.finance;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.muybien.finance.config.api.CryptoConfig;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/finances")
@RequiredArgsConstructor
public class FinanceController {

    private final FinanceProvider financeProvider;
    private final FinanceViewer financeViewer;

    @GetMapping("/{uri}")
    public ResponseEntity<Mono<Finance>> fetchFinance(
            @PathVariable String uri
    ) {
        return ResponseEntity.ok(financeProvider.fetchFinance(uri));
    }

    @GetMapping("/{uri}/{currency}")
    public ResponseEntity<Mono<Finance>> fetchFinanceWithDesiredCurrency(
            @PathVariable String uri,
            @PathVariable String currency
    ) {
        return ResponseEntity.ok(financeProvider.fetchFinanceWithDesiredCurrency(uri, currency));
    }

//    @GetMapping("/currencies")
//    public ResponseEntity<Map<String, @@>> displayAvailableCurrencies() {
//        return ResponseEntity.ok(financeViewer.displayAvailableCurrencies());
//    }

    @GetMapping("/crypto")
    public ResponseEntity<Map<String, CryptoConfig>> displayAvailableCrypto() {
        return ResponseEntity.ok(financeViewer.displayAvailableCrypto());
    }
}
