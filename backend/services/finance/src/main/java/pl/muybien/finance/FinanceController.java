package pl.muybien.finance;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.muybien.config.api.CryptoConfig;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/finances")
@RequiredArgsConstructor
public class FinanceController {

    private final FinanceServiceFactory financeServiceFactory;
    private final FinanceViewer financeViewer;

    @GetMapping("/{uri}")
    public ResponseEntity<Mono<Finance>> getFinance(
            @PathVariable String uri
    ) {
        return ResponseEntity.ok(financeServiceFactory.getService(uri).fetchCurrentFinance());
    }

    @GetMapping("/crypto")
    public ResponseEntity<Map<String, CryptoConfig>> displayAvailableCrypto() {
        return ResponseEntity.ok(financeViewer.displayAvailableCrypto());
    }
}
