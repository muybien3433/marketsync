package pl.muybien.asset;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.muybien.asset.dto.AssetAggregateDTO;
import pl.muybien.asset.dto.AssetHistoryDTO;
import pl.muybien.enums.CurrencyType;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/wallets/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService service;

    @GetMapping("/{currency}")
    public ResponseEntity<List<AssetAggregateDTO>> findAllCustomerAssets(
            @RequestHeader("X-Customer-Id") UUID customerId,
            @PathVariable("currency") CurrencyType currency
    ) {
        return ResponseEntity.ok(service.findAllCustomerAssets(customerId, currency));
    }

    @GetMapping("/history")
    public ResponseEntity<List<AssetHistoryDTO>> findAllHistoryAssets(
            @RequestHeader("X-Customer-Id") UUID customerId
    ) {
        return ResponseEntity.ok(service.findAllAssetHistory(customerId));
    }

    @PostMapping
    public ResponseEntity<String> createAsset(
            @RequestHeader("X-Customer-Id") UUID customerId,
            @RequestBody @Valid AssetRequest request
    ) {
        service.createAsset(customerId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/{asset-id}")
    public ResponseEntity<AssetHistoryDTO> updateAsset(
            @RequestHeader("X-Customer-Id") UUID customerId,
            @RequestBody @Valid AssetRequest request,
            @PathVariable("asset-id") UUID assetId
    ) {
        return ResponseEntity.ok(service.updateAsset(customerId, request, assetId));
    }

    @DeleteMapping("/{asset-id}")
    public ResponseEntity<String> deleteAsset(
            @RequestHeader("X-Customer-Id") UUID customerId,
            @PathVariable("asset-id") UUID assetId) {
        service.deleteAsset(customerId, assetId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
