package pl.muybien.finance;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.muybien.finance.dto.FinanceBaseDTO;
import pl.muybien.finance.dto.FinanceDetailDTO;
import pl.muybien.response.FinanceResponse;
import pl.muybien.enumeration.AssetType;
import pl.muybien.enumeration.CurrencyType;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/finances")
@RequiredArgsConstructor
public class FinanceController {
    private final FinanceService service;
    private final CurrencyService currencyService;

    @GetMapping("/{assetType}/{uri}")
    public ResponseEntity<FinanceResponse> findFinanceWithDefaultCurrency(
            @PathVariable("assetType") AssetType assetType,
            @PathVariable("uri") String uri
    ) {
        return ResponseEntity.ok(service.fetchFinance(assetType, uri));
    }

    @GetMapping("/{assetType}/{uri}/{currencyType}")
    public ResponseEntity<FinanceResponse> findFinanceWithDesiredCurrency(
            @PathVariable("assetType") AssetType assetType,
            @PathVariable("uri") String uri,
            @PathVariable ("currencyType") CurrencyType currencyType) {
        return ResponseEntity.ok(service.fetchFinance(assetType, uri, currencyType));
    }

    @GetMapping("/{assetType}")
    public ResponseEntity<Set<FinanceDetailDTO>> displayAvailableFinance(
            @PathVariable("assetType") AssetType assetType
    ) {
        return ResponseEntity.ok(service.displayAvailableFinance(assetType));
    }

    @GetMapping("/base/{assetType}")
    public ResponseEntity<Set<FinanceBaseDTO>> displayAvailableFinanceBase(
            @PathVariable("assetType") AssetType assetType
    ) {
        return ResponseEntity.ok(service.displayAvailableFinanceBase(assetType));
    }

    @GetMapping("/currencies/{from}/{to}")
    public ResponseEntity<BigDecimal> findExchangeRate(
            @PathVariable("from") CurrencyType from,
            @PathVariable("to") CurrencyType to
    ) {
        return ResponseEntity.ok(currencyService.findExchangeRate(from, to));
    }
}
