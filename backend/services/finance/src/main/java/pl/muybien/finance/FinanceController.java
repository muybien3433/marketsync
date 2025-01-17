package pl.muybien.finance;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/finances")
@RequiredArgsConstructor
public class FinanceController {

    private final FinanceService service;

    @GetMapping("/{asset-type}/{uri}/{currency}")
    public ResponseEntity<FinanceResponse> findFinanceByTypeAndUriAndCurrency(
            @PathVariable("asset-type") String assetType,
            @PathVariable("uri") String uri,
            @PathVariable("currency") String currency
    ) {
        return ResponseEntity.ok(service.fetchFinance(assetType, uri, currency));
    }

    @GetMapping("/{asset-type}/{uri}")
    public ResponseEntity<FinanceResponse> findFinanceWithDefaultCurrency(
            @PathVariable("asset-type") String assetType,
            @PathVariable("uri") String uri
    ) {
        return ResponseEntity.ok(service.fetchFinance(assetType, uri));
    }

    @GetMapping("/{asset-type}")
    public ResponseEntity<Set<FinanceDetail>> displayAvailableFinance(
            @PathVariable("asset-type") String assetType
    ) {
        return ResponseEntity.ok(service.displayAvailableFinance(assetType));
    }

    @GetMapping("/currencies/{from}/{to}")
    public ResponseEntity<BigDecimal> findExchangeRate(
            @PathVariable("from") String from,
            @PathVariable("to") String to
    ) {
        return ResponseEntity.ok(service.findExchangeRate(from, to));
    }
}
