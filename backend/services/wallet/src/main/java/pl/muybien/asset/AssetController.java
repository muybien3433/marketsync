package pl.muybien.asset;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.muybien.asset.dto.AssetAggregateDTO;
import pl.muybien.asset.dto.AssetHistoryDTO;

import java.util.List;

@RestController
@RequestMapping("api/v1/wallets/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService service;

    @GetMapping("/{currency}")
    public ResponseEntity<List<AssetAggregateDTO>> findAllCustomerAssets(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("currency") String currency
    ) {
        return ResponseEntity.ok(service.findAllCustomerAssets(authHeader, currency));
    }

    @GetMapping("/history")
    public ResponseEntity<List<AssetHistoryDTO>> findAllHistoryAssets(
            @RequestHeader("Authorization") String authHeader
    ) {
        return ResponseEntity.ok(service.findAllAssetHistory(authHeader));
    }

    @PostMapping
    public ResponseEntity<String> createAsset(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody @Valid AssetRequest request
    ) {
        service.createAsset(authHeader, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/{asset-id}")
    public ResponseEntity<String> updateAsset(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody @Valid AssetRequest request,
            @PathVariable("asset-id") Long assetId
    ) {
        service.updateAsset(authHeader, request, assetId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{asset-id}")
    public ResponseEntity<String> deleteAsset(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("asset-id") Long assetId) {
        service.deleteAsset(authHeader, assetId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
