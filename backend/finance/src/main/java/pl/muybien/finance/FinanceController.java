package pl.muybien.finance;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.muybien.response.FinanceResponse;
import pl.muybien.enumeration.AssetType;
import pl.muybien.enumeration.CurrencyType;

import java.math.BigDecimal;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/finances")
@RequiredArgsConstructor
public class FinanceController {

    private final FinanceService service;

    @GetMapping("/{asset-type}/{uri}")
    public ResponseEntity<FinanceResponse> findFinanceWithDefaultCurrency(
            @PathVariable("asset-type") AssetType assetType,
            @PathVariable("uri") String uri
    ) {
        return ResponseEntity.ok(service.fetchFinance(assetType, uri));
    }

    @GetMapping("/{asset-type}")
    public ResponseEntity<Set<FinanceDetailDTO>> displayAvailableFinance(
            @PathVariable("asset-type") AssetType assetType
    ) {
        return ResponseEntity.ok(service.displayAvailableFinance(assetType));
    }

    @GetMapping("/{asset-type}/currencies/{currency-type}")
    public ResponseEntity<Set<FinanceDetailDTO>> displayAvailableFinance(
            @PathVariable("asset-type") AssetType assetType,
            @PathVariable("currency-type") CurrencyType currencyType
    ) {
        return ResponseEntity.ok(service.displayAvailableFinance(assetType, currencyType));
    }

    @GetMapping("/currencies/{from}/{to}")
    public ResponseEntity<BigDecimal> findExchangeRate(
            @PathVariable("from") CurrencyType from,
            @PathVariable("to") CurrencyType to
    ) {
        return ResponseEntity.ok(service.findExchangeRate(from, to));
    }
}
