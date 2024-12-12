package pl.muybien.wallet.asset;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/wallets/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService service;

    @PostMapping
    public ResponseEntity<String> createAsset(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody @Valid AssetRequest request
    ) {
        service.createAsset(authHeader, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{asset-id}")
    public ResponseEntity<String> deleteAsset(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("asset-id") Long assetId) {
        service.deleteAsset(authHeader, assetId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
